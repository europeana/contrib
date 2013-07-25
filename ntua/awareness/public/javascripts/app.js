////////////////////////////////////////////////
// Built by Scienta (http://scienta.dk), 2013 //
////////////////////////////////////////////////

var apiUrl = "http://aniara.local:9000",
    devApiUrl = "http://aniara.local",
    a;

// Handles searching Europeana and external 
// sources through the DSP backend
ObjectSearcher = function() {
    var self = this;

    self.objectList = ko.observableArray([]);
    self.droppedObject = ko.observable(null);
    self.showSearchPanel = ko.observable(false);
    self.filter = ko.observable('');
    self.filter.subscribe(function(value){
        self.searchFiles();
    });
    self.sourceList = [{title: 'Europeana', key: 'europeana'}, {title: 'DSP', key: 'dsp'}];
    self.source = ko.observable(self.sourceList[0].key);
    self.source.subscribe(function(value){
        if (self.showSearchPanel()) {
            self.searchFiles();
        }
    });
    self.term = ko.observable('');
    self.storedTerm = '';
    self.searchCompleted = ko.observable(false);
    self.itemsCount = 0;
    self.totalItems = ko.observable(0);
    self.pageNumber = ko.observable(1);
    self.objectsPerPage = 6;
    self.hasPrevious = ko.computed(function(){
        return self.pageNumber() > 1;
    });
    self.hasNext = ko.computed(function(){
        return self.totalItems() > 6*self.pageNumber();
    });
    self.changePage = function(direction) {
        a.storyList.previewObject(null);
        if (direction < 0 && self.pageNumber() > 1) {
            self.pageNumber(self.pageNumber()-1);
            self.searchFiles();
        }
        if (direction > 0 && self.pageNumber() < Math.ceil(self.totalItems()/self.itemsCount)) {
            self.pageNumber(self.pageNumber()+1);
            self.searchFiles();
        }
    };
    self.searchFiles = function() {
        if (!self.term()) {
            return;
        }
        a.storyList.previewObject(null);
        self.showSearchPanel(true);
        self.searchCompleted(false);

        if (self.storedTerm !== self.term()+self.filter()+self.source()) {
            self.pageNumber(1);
            self.storedTerm = self.term()+self.filter()+self.source();
        }
        // Clear old results
        self.objectList([]);
        var defer = $.Deferred();
        if (self.source() == 'dsp') {
            $.when(self.searchDSP()).then(function(){
                self.searchCompleted(true);
            });
        } else {
            $.when(
                $.ajax({
                    url: apiUrl+"/awareness/EuropeanaSearch/search",
                    contentType: 'application/json',
                    type: "POST",
                    data: ko.toJSON({pageNumber:self.pageNumber(), term:self.term(), type:self.filter()})
                }).pipe(function(data){
                    var requests = [];
                    self.itemsCount = data.itemsCount;
                    self.totalItems(data.totalResults);

                    // Clear old results again if multiple quick clicks
                    self.objectList([]);

                    if (self.itemsCount > 0) {
                        for (var i = data.items.length - 1; i >= 0; i--) {
                            switch (data.items[i].type) {
                                case 'IMAGE':
                                case 'VIDEO':
                                case 'SOUND':
                                case 'TEXT':
                                    data.items[i].type = data.items[i].type.toLowerCase();
                                    break;
                                case '_3D':
                                    data.items[i].type = '3d';
                                    break;
                                default:
                                    // alert(a.translation().errors.unsupportedobjecttype);
                                    continue;
                            }
                            requests.push(self.objectList.unshift(new ForeignObject(data.items[i])));
                        }
                    }
                    $.when.apply($, requests).then(function() {
                        a.user().fileFilter('files');
                        a.user().showUserFilePanel(true);

                        defer.resolve();
                    });
                    return defer.promise();
                }).fail(function(){
                    defer.resolve();
                    alert(a.translation().errors.searchfailed);
                })
            ).then(function(){
                self.searchCompleted(true);
            });
        }
    };

    self.searchDSP = function() {
        var defer = $.Deferred();
        offset = (self.pageNumber()-1)*self.objectsPerPage;
        $.get(apiUrl+"/awareness/Search?theme="+a.selectedTheme().title+"&profile=image&query="+self.term()+'&start='+offset+'&max_results='+self.objectsPerPage).then(function(data){
            var requests = [];
            self.totalItems(data.searchMeta.hits);
            self.itemsCount = data.searchMeta.count;

            // Clear old results again if multiple quick clicks
            self.objectList([]);

            if (self.itemsCount > 0) {
                for (i = data.images.length - 1; i >= 0; i--) {
                    data.images[i].type = 'image';
                    requests.push(self.objectList.unshift(new StoryObject(data.images[i])));
                };
            }
            $.when.apply($, requests).always(function() {
                a.user().fileFilter('files');
                a.user().showUserFilePanel(true);

                defer.resolve();
            });
        }, function(){
            defer.resolve();
            alert(a.translation().errors.searchfailed);
        });
        return defer.promise();
    };
};

// Storytelling user
User = function(data){
    var self = this;

    // data
    if (data.id)
        self.id = data.id;
    if (data._id)
        self.id = data._id.$oid;
    self.username = ko.observable(data.username);
    self.email = ko.observable(data.email);
    self.role = ko.observable(data.role);
    self.objects = ko.observableArray([]);
    self.objectPage = ko.observable(1);
    self.objectsPerPage = 5;
    self.objectsForPage = ko.computed(function(){
        start = (self.objectPage()-1)*self.objectsPerPage;
        return self.objects.slice(start, self.objectsPerPage + start);
    });
    self.stories = ko.observableArray([]);
    self.storyPage = ko.observable(1);
    self.storiesPerPage = 5;
    self.storiesForPage = ko.computed(function(){
        start = (self.storyPage()-1)*self.storiesPerPage;
        return self.stories.slice(start, self.storiesPerPage + start);
    });

    self.showUserFilePanel = ko.observable(false);
    self.fileFilter = ko.observable('files');
    self.fileFilter.subscribe(function(){
        a.storyList.previewObject(null);
    });
    self.hasPrevious = ko.computed(function(){
        if (self.fileFilter() == 'files')
            return self.objectPage() > 1;

        return self.storyPage() > 1;    
    });
    self.hasNext = ko.computed(function(){
        if (self.fileFilter() == 'files')
            return self.objects().length > self.objectsPerPage*self.objectPage();

        return self.stories().length > self.storiesPerPage*self.storyPage();
    });
    self.showUserFilePanel.subscribe(function(value){
        if (value == true) {
            self.loadLibrary();
            self.showUserUploadPanel(false);
        }
        a.storyList.previewObject(null);
    });
    self.showUserUploadPanel = ko.observable(false);
    self.showUserUploadPanel.subscribe(function(value){
        if (value == true) {
            createUploader();
            self.showUserFilePanel(false);
        }
        a.storyList.previewObject(null);
    });

    self.changePage = function(direction) {
        a.storyList.previewObject(null);
        if (self.fileFilter() == 'files') {
            if (direction < 0 && self.objectPage() > 1) {
                self.objectPage(self.objectPage()-1);
            }
            if (direction > 0 && self.objectPage() < Math.ceil(self.objects().length/self.objectsPerPage)) {
                self.objectPage(self.objectPage()+1);
            }
        } else {
            if (direction < 0 && self.storyPage() > 1) {
                self.storyPage(self.storyPage()-1);
            }
            if (direction > 0 && self.storyPage() < Math.ceil(self.stories().length/self.storiesPerPage)) {
                self.storyPage(self.storyPage()+1);
            }
        }
    };

    self.didLoadObjects = ko.observable(false);
    self.didLoadStories = ko.observable(false);
    self.loadLibrary = function(){
        var requests = [];

        if (!self.didLoadObjects()) {
            // console.log('loadLibrary objects');

            // Load objects
            $.get(apiUrl+"/awareness/Users/"+self.id+"/objects").then(function(result){
                self.objects([]);
                for (var i = result.values.length - 1; i >= 0; i--) {
                    requests.push(self.objects.unshift(new StoryObject(result.values[i])));
                }
                $.when.apply($, requests).then(function(){
                    self.didLoadObjects(true);
                });
            });
        }
        if (!self.didLoadStories()) {
            // console.log('loadLibrary stories');
            
            // Load stories
            $.get(apiUrl+"/awareness/Users/"+self.id+"/stories").then(function(result){
                    self.stories([]);
                for (var i = result.values.length - 1; i >= 0; i--) {
                    result.values[i].creator = self.id;
                    requests.push(self.loadStory(result.values[i]));
                }
                $.when.apply($, requests).then(function(){
                    self.didLoadStories(true);
                });
            });
        }
    };

    self.loadStory = function(data){
        var defer = $.Deferred(),
            story = new Story(data);
        $.when(story.loadObjects()).then(function(){
            self.stories.unshift(story);
            defer.resolve();
        }, function(){
            defer.resolve();
        });
        return defer.promise();
    };

    self.hasObject = function(object) {
        if (object instanceof StoryObject) {
            for (var i = self.objects().length - 1; i >= 0; i--) {
                if (self.objects()[i].id == object.id) {
                    return true;
                }
            };
        }
        return false;
    };

    self.addObject = function(object) {
        if (object instanceof StoryObject) {
            if (self.hasObject(object)) {
                return;
            }
            data = {title: object.title, 
                    description: object.description,
                    provider: object.provider,
                    dataProvider: object.dataProvider,
                    url: object.url, 
                    // don't send thumbnail if this is a Story Object
                    thumbnail: object.thumbnail().indexOf("images/") == 0 ? null : object.thumbnail(),
                    type: object.type,
                    license: object.license,
                    dcCreator: object.author(),
                    source: object.source, 
                    sosource: object.sosource,
                    storyImage: object.storyImage()
            };
        } else {
            data = {type: object.type,
                    title: object.title, 
                    url: object.url, 
                    provider: object.provider,
                    dataProvider: object.dataProvider,
                    license: object.license,
                    dcCreator: object.author(),
                    source: object.source, 
                    sosource: object.sosource,
                    europeanaid: object.europeanaid
            };
        }
        a.storyList.previewObject(null);
        newObject = new StoryObject(data);
        $.when(newObject.save()).then(function(){
            self.objects.unshift(newObject);
            self.fileFilter('files');
            self.showUserFilePanel(true);
        });
    };
    self.removeObject = function(object) {
        if (object instanceof StoryObject && confirm(a.translation().userfilepanel.deleteObjectConfirm)) {
            $.ajax(apiUrl+"/awareness/digitalObjects/delete?id="+object.id, {
                contentType: 'application/json',
            }).then(function(){
                self.objects.remove(object);
            });
            a.storyList.previewObject(null);
        }
    };
    self.removeObjectEverywhere = function(object) {
        if (object instanceof StoryObject && confirm(a.translation().userfilepanel.deleteObjectConfirm)) {
            self.url = apiUrl+"/awareness/Admin/deleteObject?id="+object.id;
            if (object.sosource !== 'Europeana') {
                self.url = apiUrl+"/awareness/StoryImages/delete?id="+object.storyImage();
            }
            $.ajax(self.url, {
                contentType: 'application/json',
            }).then(function(){
                self.objects.remove(object);
            });
            a.storyList.previewObject(null);
        }
    };

    self.uploadFile = function() {
        if (!$('#upload_title').val() || !$('#upload_description').val() || !$('#uploadImage_upfile').val()) {
            alert(a.translation().errors.objectuploadmissing);
            return;
        }
        self.showUserUploadPanel(false);
        $.ajax({
            url: apiUrl+'/awareness/StoryImages/save',
            contentType :"application/json",
            type: 'POST',
            data: ko.toJSON({
                title: $('#upload_title').val(), 
                description: $('#upload_description').val(),
                original:$('#uploadImage_upfile').val(),
                originalName:$('#uploadImage_httpup').val()
            }),
            dataType: 'json',
            success: function (d) {
                self.didLoadObjects(false);
                self.showUserFilePanel(true);
               },
               error: function(xhr) {
                   alert(a.translation().errors.objectuploadfailed);
            }
        });
    };

    self.removeStory = function(object) {
        if (object instanceof Story && confirm(a.translation().userfilepanel.deleteStoryConfirm)) {
            $.ajax(apiUrl+"/awareness/digitalStories/delete?id="+object.id, {
                contentType: 'application/json',
            }).then(function(){
                a.storyList.selectedStory(null);
                a.storyBuilder.showStoryPanel(false);
                self.stories.remove(object);
                a.storyList.stories.remove(object);
                a.storyList.sortByProperty(a.storyList.sortedBy());

                alert(a.translation().userfilepanel.deleteStoryComplete);
            });
        }
    };

    self.save = function(password, password_confirmation) {
        var defer = $.Deferred();

        error = false;
        if (!error && self.username().length < 3 || !self.username().match(/^[a-zA-Z0-9\-_\.@]+$/)) {
            error = true;
            alert(a.translation().errors.invalidusername);
        }
        if (!error && self.email().length < 1) {
            error = true;
            alert(a.translation().errors.invalidemail);
        }
        if (!error && !self.id && password.length < 6) {
            error = true;
            alert(a.translation().errors.invalidpassword);
        }
        if (self.id && !password) {
            error = true;
            alert(a.translation().errors.passwordmissing);
        }

        data = {
            username: self.username(), 
            email: self.email(),
            role: self.role()
        };
        if (self.id) {
            data.id = self.id;
        }
        if (password) {
            if (password !== password_confirmation) {
                error = true;
                alert(a.translation().errors.passwordmismatch);
            }
            data.password = password;
            data.password_confirmation = password_confirmation;
        }

        if (error) {
            return defer.reject();
        }

        $.ajax(
            apiUrl+"/awareness/Users/save", {
            contentType: 'application/json',
            // dataType : 'json',
            type: "POST",
            data: ko.toJSON(data)}
        ).pipe(function(result){
            self.id = result.id;
            defer.resolve();
        }).fail(function(){
            alert(a.translation().errors.usersavefailed);
            defer.reject();
        });

        return defer.promise();
    };
};

ForeignObject = function(data) {
    var self = this;

    // data
    self.europeanaid = data.europeanaid;
    self.type = data.type;
    self.source = data.source;
    self.title = ko.observable(data.title);
    self.author = ko.observable(data.dcCreator ? data.dcCreator : null);
    self.provider = data.provider;
    self.dataProvider = data.dataProvider;
    self.license = data.license;
    self.licenseArray = self.license ? self.license.split(',') : [];
    self.url = data.url;
    self.sosource = 'Europeana';

    self.isEditing = ko.observable(false);
};
ForeignObject.prototype.content = function() {
    var self = this;
    switch (self.type) {
        case 'image':
        case 'video':
        case 'sound':
        case 'text':
        case '3d':
            if (typeof self.url == 'undefined')
                return;
            return '<img src="'+self.url+'">';
            // return '<iframe width="311" height="175" src="'+self.url+'" frameborder="0" allowfullscreen></iframe>';
            break;
        default:
            return 'invalid';
            break;
    }
};
ForeignObject.prototype.showPreview = function(data, event){
    var self = this;
    if (a.storyList.previewObject() == self) {
        a.storyList.previewObject(null);
        return;
    }
    a.storyList.previewObject(self);
    cube = $(event.target);
    left = cube.offset().left+(cube.width()/2);
    if (left+$('#object_preview').outerWidth() > $('body').innerWidth()) {
        left -= $('#object_preview').outerWidth();
    }
    $('#object_preview').offset({left: left, top: cube.offset().top+(cube.height()/2)});
};


StoryObject = function(data, story){
    var self = this;

    // data
    self.title = ko.observable('');
    self.description = ko.observable('');
    self.provider = '';
    self.creator = ko.observable(null);
    self.dcCreator = '';
    self.dataProvider = '';
    self.source = data.source ? data.source : '';
    self.tags = data.tags ? data.tags : [];
    self.type = data.type;
    self.url = '';
    self.id = null;
    self.language = 'en';
    self.dateCreated = null;
    self.license = '';
    self.licenseArray = [];
    self.sosource = data.sosource ? data.sosource : '';
    self.thumbnail = ko.observable(null);
    self.europeanaid = data.europeanaid ? data.europeanaid : '';
    self.storyImage = ko.observable(null);

    // internal
    self.scriptPart = ko.observable('');
    self.scriptFilter = ko.computed({
        read: function(){ return self.scriptPart().replace(/&lt;/g, '<').replace(/&gt;/g,'>'); },
        write: function(value) { self.scriptPart(value.replace(/</g,'&lt;').replace(/>/g,'&gt;')); },
        owner: this
    });
    self.story = story;
    self.isEditing = ko.observable(false);
    self.author = ko.observable('');

    self.setData = function(data) {
        if (data._id)
            self.id = data._id.$oid;
        if (data.title)
            self.title(data.title);
        if (data.description)
            self.description(data.description);
        if (data.scriptPart)
            self.scriptPart(data.scriptPart);
        if (data.dcCreator)
            self.dcCreator = data.dcCreator;
        if (data.theme)
            self.theme = data.theme; 
        if (data.language)
            self.language = data.language;
        if (data.license) {
            self.license = data.license;
            self.licenseArray = self.license ? self.license.split(',') : [];
        }

        if (data.storyImage)
            self.storyImage(data.storyImage);
        if (data.url)
            self.url = data.url;
        if (data.thumbnail)
            self.thumbnail(data.thumbnail);
        if (data.objectPreview) {
            self.url = "../images/"+data.objectPreview;
            self.thumbnail("../images/"+data.objectThumbnail);
            self.storyImage(self.id);
        }

        if (data.dateCreated)
            self.dateCreated = Date.parse(data.dateCreated.$date.replace(/T/, ' ').replace(/.\d{3}Z/, '').replace(/-/g, '/'));

        if (data.creator && typeof data.creator !== 'undefined') {
            self.creator(data.creator);

            if (a.loggedIn() && self.creator() == a.user().id) {
                self.author(a.user().username());
            } else {
                $.ajax(apiUrl+"/awareness/Users/"+data.creator, {
                    contentType: 'application/json',
                    success: function(result) {
                        self.author(result.username);
                    }
                });
            }
        }

        if (data.provider) {
            self.provider = data.provider;
        }
        if (data.dataProvider) {
            self.dataProvider = data.dataProvider;
        }
    };

    if (data) {
        self.setData(data);
    }

    // function references
    self.image = ko.computed(self.image, self);
    self.objectType = ko.computed(function(){
        return 'object_'+self.type;
    }, self);
};

StoryObject.prototype.image = function(){
    var self = this;

    if (self.thumbnail()) {
        return self.thumbnail();
    }

    return 'images/cubes/cube_blank.png';
};
StoryObject.prototype.content = function() {
    var self = this;
    switch (self.type) {
        case 'image':
        case 'video':
        case 'sound':
        case 'text':
        case '3d':
            if (typeof self.url == 'undefined')
                return;
            return '<img src="'+self.url+'">';
            break;
        // case 'video':
            // return '<object width="311" height="175"><param name="movie" value="'+self.url+'"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="'+self.url+'" type="application/x-shockwave-flash" width="311" height="175" allowscriptaccess="always" allowfullscreen="true"></embed></object><p>'+self.description+'</p>';
            // return '<iframe width="311" height="175" src="'+self.url+'" frameborder="0" allowfullscreen></iframe><p>'+self.scriptPart()+'</p>';
            break;
        default:
            return 'invalid';
            break;
    }
};
StoryObject.prototype.showPreview = function(data, event){
    var self = this;
    if (a.storyList.previewObject() == self) {
        self.isEditing(false);
        a.storyList.previewObject(null);
        return;
    }
    a.storyList.previewObject(self);
    cube = $(event.target);
    left = cube.offset().left+(cube.width()/2);
    if (left+$('#object_preview').outerWidth() > $('body').innerWidth()) {
        left -= $('#object_preview').outerWidth();
    }
    $('#object_preview').offset({left: left, top: cube.offset().top+(cube.height()/2)});
};
StoryObject.prototype.showScriptPreview = function(data, event){
    var self = this;

    self.showPreview(data, event);
    self.isEditing(true);
};
StoryObject.prototype.save = function(){
    var self = this;

    return $.ajax(apiUrl+"/awareness/digitalObjects/save", {
        contentType: 'application/json',
        // dataType : 'json',
        type: "POST",
        data: ko.toJSON({
            title: self.title(), 
            description: self.description(),
            provider: self.provider,
            creator: a.user().id,
            dcCreator: self.dcCreator,
            dataProvider: self.dataProvider,
            source: self.source,
            type: self.type,
            url: self.url,
            language: self.language,
            license: self.license,
            sosource: self.sosource,
            thumbnail: self.thumbnail(),
            europeanaid: self.europeanaid,
            storyImage: self.storyImage()
        })}
    ).pipe(function(result){
        self.setData(result);
    }).fail(function(result){
        if (result.responseText) {
            alert(result.responseText);
        } else {
            alert(a.translation().errors.objectsavefailed);
        }
    });
};

StoryBuilder = function() {
    var self = this;

    self.newStory = ko.observable(new Story());
    self.showStoryPanel = ko.observable(false);
    self.showStoryPanel.subscribe(function(value){
        a.storyList.previewObject(null);
    });
    self.storyProgress = ko.observable(1);
    self.storyProgress.subscribe(function(value){
        if (value == 2) {
            a.user().fileFilter('files');
            a.user().showUserFilePanel(true);
            self.newStory().save();
        }
    });
    self.progressStory = function(step) {
        if (step < 0 && self.storyProgress() == 2) {
            self.storyProgress(self.storyProgress()-1);
        }
        if (step > 0 && self.storyProgress() == 1) {
            self.storyProgress(self.storyProgress()+1);
        }
    };

    self.editStory = function(story) {
        // Create a new story and hide the player
        if (story == null) {
            story = new Story();
            story.language(a.selectedLanguage());
            story.theme(a.selectedTheme().id);
            a.storyList.selectedStory(null);
        }
        self.newStory(story);
        self.storyProgress(1);
        self.showStoryPanel(true);
    };

    self.autoInsertStoryObject = function(object) {
        slot = self.newStory().firstAvailableObjectSlot();
        if (slot < 0) {
            alert(a.translation().errors.storyFull);
            return;
        }
        self.showStoryPanel(true);
        self.storyProgress(2);
        a.storyList.previewObject(false);
        self.insertStoryObject(object, slot);
    };

    self.insertStoryObject = function(object, index) {
        if (typeof index == 'undefined') {
            index = 0;
        }
        if (self.newStory().hasObject(object)) {
            return;
        }
        a.storyList.previewObject(false);
        object.story = self.newStory();
        self.newStory().objects.splice(index, 1, object);
        setTimeout(function(){
            $('#storyBuilderGrid #objectId_'+object.id).click();
        }, 50);

        self.newStory().save();
    };
    self.removeStoryObject = function(index) {
        if (typeof index == 'undefined') {
            index = 0;
        }
        if (self.newStory().objects()[index])
            self.newStory().objects()[index].story = null;

        self.newStory().objects.splice(index, 1, null);
        self.newStory().save();
        a.storyList.previewObject(false);
    };
    self.removeStoryObjectById = function(id) {
        for (var i = self.newStory().objects().length - 1; i >= 0; i--) {
            if (self.newStory().objects()[i] && self.newStory().objects()[i].id == id) {
                return self.removeStoryObject(i);
            }
        };
    };
};

Story = function(data){
    var self = this;

    // data
    self.id = null;
    self.firstObject = null;
    self.objects = ko.observableArray([]);
    self.comments = ko.observableArray([]);
    self.isPublished = ko.observable(false);
    self.isPublishable = ko.observable(false);
    self.selectedObject = ko.observable(null);
    self.selectedObject.subscribe(function(value){
        $('.objectcolumn iframe').attr('src','').hide();
    });
    self.creator = ko.observable(null);
    self.author = ko.observable('Unknown');
    self.coverImage = ko.observable(null);
    self.hasImage = ko.observable(false);
    self.likes = ko.observable(0);
    self.comments = ko.observableArray([]);
    self.title = ko.observable('');
    self.description = ko.observable('');
    self.descriptionFilter = ko.computed({
        read: function(){ return self.description().replace(/&lt;/g, '<').replace(/&gt;/g,'>'); },
        write: function(value) { self.description(value.replace(/</g,'&lt;').replace(/>/g,'&gt;')); },
        owner: this
    });
    self.tags = ko.observableArray([]);
    self.tagsAsString = ko.computed({
        read: function(){
            return self.tags().join(', ');
        },
        write: function(value){
            self.tags(value.split(', '));
        }
    });
    self.theme = ko.observable();
    self.language = ko.observable();
    self.date = '';
    self.license = '';
    self.interactionPanel = ko.observable(null);
    self.rawData = null;

    var tmpObjects = [];
    for (var i = 0; i < 12; i++) {
        tmpObjects[i] = null;
    }
    self.objects(tmpObjects);

    self.firstAvailableObjectSlot = function() {
        for (var i = 0; i < self.objects().length; i++) {
            if (self.objects()[i] == null) {
                return i;
            }
        };
        return -1;
    };
    self.hasObject = function(object) {
        if (object instanceof StoryObject) {
            for (var i = self.objects().length - 1; i >= 0; i--) {
                if (self.objects()[i] && self.objects()[i].id == object.id) {
                    return true;
                }
            };
        }
        return false;
    };
    self.loadComments = function(){
        if (!self.id)
            return;

        $.get(apiUrl+"/awareness/digitalStories/"+self.id+"/comments").then(function(result){
            for (var i = result.values.length - 1; i >= 0; i--) {
                self.comments.push(new StoryComment(result.values[i]));
            }
        });
    };
    self.addComment = function(){
        // Don't save empty comments
        if (!$('#commentmessage').val()) {
            return;
        }

        data = {
            text: $('#commentmessage').val(),
            userId: a.user().id,
            storyId: self.id
        };
        $.ajax(apiUrl+"/awareness/digitalStories/comments/save", {
            contentType: 'application/json',
            // dataType : 'json',
            type: "POST",
            data: ko.toJSON(data)}
        ).pipe(function(result){
            $('#commentmessage').val('');
            self.comments.push(new StoryComment(result));
        });
    };
    self.removeComment = function(comment){
        $.ajax(apiUrl+"/awareness/digitalStories/comments/delete?id="+comment.id, {
            contentType: 'application/json'
        }).then(function(){
            self.comments.remove(comment);
        });    
    };


    self.setData = function(data) {
        self.id = data._id.$oid;
        self.title(data.title);
        self.description(data.description);
        self.theme(data.theme);
        self.language(data.language);
        self.license = data.license;
        self.isPublished(data.isPublished);
        self.isPublishable(data.isPublishable);

        if (data.tags)
            self.tags(data.tags);

        if (data.dateCreated)
            self.date = Date.parse(data.dateCreated.$date.replace(/T/, ' ').replace(/.\d{3}Z/, '').replace(/-/g, '/'));
        if (data.creator && typeof data.creator !== 'undefined') {
            self.creator(data.creator);

            if (a.loggedIn() && self.creator() == a.user().id) {
                self.author(a.user().username());
            } else {
                $.ajax(apiUrl+"/awareness/Users/"+data.creator, {
                    contentType: 'application/json',
                    success: function(result) {
                        self.author(result.username);
                    }
                });
            }
        }
    };

    if (data) {
        self.rawData = data;
        self.setData(data);
        self.loadComments();
    }

    self.canEdit = function(){
        if (!a.loggedIn())
            return false;
        if (self.creator() != a.user().id && a.user().role() == 'contributor')
            return false;

        return true;
    };

    self.loadObjects = function(){
        var defer = $.Deferred(),
            requests = [];

        // Return early from stories without objects
        if (!self.rawData.storyObjects) {
            return;
        }
        for (var i = self.rawData.storyObjects.length - 1; i >= 0; i--) {
            if (typeof self.rawData.storyObjects[i].StoryObjectID == 'undefined') {
                continue;
            }
            requests.push(
                $.ajax({
                    url: apiUrl+"/awareness/digitalObjects/"+self.rawData.storyObjects[i].StoryObjectID,
                    type: "GET", 
                    index: i})
                .then(function(result){
                    result.scriptPart = self.rawData.storyObjects[this.index].scriptPart;
                    tmpObjects[self.rawData.storyObjects[this.index].position] = new StoryObject(result, self);
                    if (this.index == 0) {
                        self.firstObject = tmpObjects[self.rawData.storyObjects[this.index].position];
                    }
                })
            );
        }
        $.when.apply($, requests).then(function() {
            // console.log('loading object  for '+self.title());
            self.objects(tmpObjects);
            self.selectedObject(self.firstObject);
            defer.resolve();
        }, function() {
            // console.log('failed object for '+self.title());
            defer.resolve();
        });

        return defer.promise();
    };

    self.flag = function(){};
    self.comment = function(){};
};
Story.prototype.navigateSelectedObject = function(direction) {
    var self = this;

    self.selectedObject(self.findNextObject(direction, true));
};

Story.prototype.findNextObject = function(direction, allowLoop) {
    var self = this;

    currentIndex = self.objects.indexOf(self.selectedObject());
    var i = 0;
    // Next
    if (direction > 0) {
        for (i = currentIndex + 1; i < self.objects().length; i++) {
            if (self.objects()[i] == null) {
                continue;
            }
            return self.objects()[i];
        };
        if (!allowLoop) {
            return null;
        }
        // didn't find one going forward, start over
        for (i = 0; i < currentIndex; i++) {
            if (self.objects()[i] == null) {
                continue;
            }
            return self.objects()[i];
        };
    }
    // Previous
    if (direction < 0) {
        for (i = currentIndex - 1; i >= 0; i--) {
            if (self.objects()[i] == null) {
                continue;
            }
            return self.objects()[i];
        };
        if (!allowLoop) {
            return null;
        }
        // didn't find one going backwards, start over
        for (i = self.objects().length; i >= currentIndex; i--) {
            if (self.objects()[i] == null) {
                continue;
            }
            return self.objects()[i];
        };
    }
};
Story.prototype.save = function(){
    var self = this;

    if (!self.creator()) {
        if (!a.loggedIn) {
            alert(a.translation().errors.notloggedin);
            return;
        }
        self.creator(a.user().id);
    }

    data = {
        title: self.title, 
        description: self.description,
        tags: self.tags,
        creator: self.creator(),
        theme: self.theme(),
        language: self.language(),
        isPublished: self.isPublished(),
        isPublishable: self.isPublishable(),
        storyObjects: self.serializedObjects()
    };
    if (self.id) {
        data._id = self.id;
    }
    return $.ajax(apiUrl+"/awareness/digitalStories/save", {
        contentType: 'application/json',
        // dataType : 'json',
        type: "POST",
        data: ko.toJSON(data)}
    ).pipe(function(result){
        // console.log('saving new story '+self.title());
        // New story, add to library
        if (!self.id) {
            a.user().stories.unshift(self);
        }
        self.setData(result);
    });
};
Story.prototype.publish = function(){
    var self = this;

    if (self.isPublishable()) {
        // Newly published
        if (!self.isPublished()) {
            if (self.theme() == a.selectedTheme().id) {
                // Same theme, add to browser
                a.storyList.stories.unshift(self);
                a.storyList.sortByProperty(a.storyList.sortedBy());
            } else {
                // Switch to story theme
                a.selectedTheme(ko.utils.arrayFirst(a.themes(), function(item){
                    return item.id == self.theme();
                }));
            }
        }
        a.storyBuilder.showStoryPanel(false);
        a.user().showUserFilePanel(false);
        a.storyList.selectedStory(null);

        self.isPublished(true);
        self.save();
    }
};
Story.prototype.serializedObjects = function(){
    var self = this;

    data = [];
    for (var i = self.objects().length - 1; i >= 0; i--) {
        if (self.objects()[i] == null) {
            continue;
        }
        data.unshift({StoryObjectID: self.objects()[i].id, position: i, order: i, scriptPart: self.objects()[i].scriptPart()});
    };
    return data;
};
Story.prototype.objectCount = function(){
    var self = this;

    count = 0;
    for (var i = self.objects().length - 1; i >= 0; i--) {
        if (self.objects()[i] == null) {
            continue;
        }
        count++;
    };
    return count;
};

StoryComment = function(data){
    var self = this;

    // data
    self.id;
    self.userId = ko.observable();
    self.storyId = ko.observable();
    self.text = ko.observable();
    self.dateCreated;
    self.dateFormated = ko.computed({
        read: function(){
            return new Date(self.dateCreated).toLocaleDateString();
        },
        deferEvaluation: true
    });

    // Internal
    self.author = ko.observable('');

    self.setData = function(data) {
        if (data._id)
            self.id = data._id.$oid;
        if (data.storyId)
            self.storyId(data.storyId);
        if (data.text)
            self.text(data.text);

        if (data.dateCreated)
            self.dateCreated = Date.parse(data.dateCreated.$date.replace(/T/, ' ').replace(/.\d{3}Z/, '').replace(/-/g, '/'));

        if (data.userId) {
            self.userId(data.userId);

            if (a.loggedIn() && self.userId() == a.user().id) {
                self.author(a.user().username());
                return;
            }

            $.ajax(apiUrl+"/awareness/Users/"+self.userId(), {
                contentType: 'application/json',
                success: function(result) {
                    self.author(result.username);
                }
            });
        }
    };

    if (data) {
        self.setData(data);
    }

    self.canRemove = ko.computed(function(){
        if (!a.loggedIn())
            return false;
        if (self.userId() != a.user().id && a.user().role() == 'contributor')
            return false;

        return true;
    });
};

StoryList = function(){
    var self = this;

    // data
    self.term = ko.observable('');
    self.storedTerm = null;
    self.totalStories = 0;
    self.searchCompleted = ko.observable(false);
    self.hideStories = ko.observable(true);
    self.searchCompleted.subscribe(function(value){
        if (value) {
            self.sortByProperty(self.sortedBy());
        } else if (self.hideStories()) {
            $('#stories').hide();
        }
    });

    self.sortedBy = ko.observable('date');
    self.storyOffset = ko.observable(0);
    self.storedOffset = null;
    self.storiesPerPage = 16;
    self.stories = ko.observableArray([]);
    self.selectedStory = ko.observable(null);
    self.previewedObject = ko.observable(null);
    self.previewObject = ko.computed({
        read: function(){ return self.previewedObject(); },
        write: function(value){
            if (!value && self.previewedObject.peek()) {
                if (self.previewedObject().isEditing()) {
                    a.storyBuilder.newStory().save();
                }
                self.previewedObject().isEditing(false);
            }
            $('#object_preview_player iframe').attr('src','').hide();
            self.previewedObject(value); 
        }
    });

    // methods
    self.loadNext = function() {
        if (self.storyOffset()+self.storiesPerPage < self.totalStories) {
            self.hideStories(false);
            self.storyOffset(self.storyOffset()+self.storiesPerPage);
            self.searchStories();
            self.hideStories(true);
        }
    };
    self.reloadStories = function(){
        self.storedTerm = null;
        self.selectedStory(null);
        self.previewObject(null);
        self.storyOffset(0);
        self.stories([]);
        self.searchStories();
    };
    self.clearSearch = function(){
        self.term('');
        self.searchStories();
    };
    self.searchStories = function() {
        if (self.storedTerm == self.term() && self.storedOffset == self.storyOffset()) {
            return;
        }
        if (self.storedOffset != self.storyOffset()) {
            self.storedOffset = self.storyOffset();
        }
        if (self.storedTerm != self.term()) {
            self.storedTerm = self.term();
            self.selectedStory(null);
            self.previewObject(null);
            self.stories([]);
            self.storyOffset(0);
        }
        self.searchCompleted(false);

        if (self.term() == '') {
            return self.loadStories();
        }

        var defer = $.Deferred();
        $.get(apiUrl+"/awareness/Search?theme="+a.selectedTheme().title+"&profile=story&query="+self.term()+'&start='+self.storyOffset()+'&max_results='+self.storiesPerPage).then(function(data){
            self.totalStories = data.searchMeta.hits;
            var requests = [];
            for (i = data.digitalStories.length - 1; i >= 0; i--) {
                requests.push(self.loadStory(data.digitalStories[i].id));
            };
            $.when.apply($, requests).always(function() {
                self.searchCompleted(true);
                defer.resolve();
            });
        }, function(){
            self.searchCompleted(true);
            alert(a.translation().errors.storyloadfailed);
        });
        return defer.promise();
    };
    self.loadStories = function(){
        self.searchCompleted(false);
        var defer = $.Deferred();
        $.get(apiUrl+"/awareness/Themes/"+a.selectedTheme().id+"/stories?from="+self.storyOffset()+'&to='+(self.storyOffset()+self.storiesPerPage)).then(function(data){
            self.totalStories = data.totalSize;
            var requests = [];
            for (i = data.stories.length - 1; i >= 0; i--) {
                requests.push(self.loadStory(data.stories[i].id));
            };
            $.when.apply($, requests).always(function() {
                self.searchCompleted(true);
                defer.resolve();
            });
        }, function(){
            self.searchCompleted(true);
            alert(a.translation().errors.storyloadfailed);
        });
        return defer.promise();
    };
    self.loadStory = function(id) {
        var defer = $.Deferred();

        $.get(apiUrl+"/awareness/digitalStories/"+id).then(function(result){
            if (!result.storyObjects) {
                return defer.resolve();
            }
            var story = new Story(result);
            $.when(story.loadObjects()).then(function(){
                if (result.coverImage) {
                    story.coverImage(new StoryObject({url: result.coverImage}), self);
                }

                // console.log('loadStory '+story.id);
                self.stories.push(story);
                defer.resolve();
            }, function() {
                defer.resolve();
            });
        }, function(){
            defer.resolve();
        });
        return defer.promise();
    };
    self.selectStoryById = function(id) {
        if (self.selectedStory() && self.selectedStory().id == id) {
            return;
        }
        for (var i = self.stories().length - 1; i >= 0; i--) {
            if (self.stories()[i].id == id) {
                return self.selectedStory(self.stories()[i]);
            }
        };
        // story is missing
        $.get(apiUrl+"/awareness/digitalStories/"+id).then(function(result){
            if (!result.storyObjects) {
                return;
            }
            var story = new Story(result);

            // Don't show unpublished stories unless user owns it
            if (!story.isPublished()){
                if (!a.loggedIn()) {
                    return self.selectedStory(null);
                } else if (story.creator() !== a.user().id) {
                    return self.selectedStory(null);
                }
            }
            $.when(story.loadObjects()).then(function(){
                self.selectedStory(story);
            });
        });
    };
    self.sortByProperty = function(name){
        self.sortedBy(name);
        self.stories.sortBy(name);
        $('#stories').imagesLoaded(function(){
            $('#stories').show();
            $('#stories').masonry('reload');
        });
    };

    self.shareStory = function(service){
        if (service == 'twitter') {
            window.open ("https://twitter.com/intent/tweet?url="+encodeURIComponent(location.href)+"&text="+encodeURI(a.translation().interactionpanel.tweetText+" '"+self.selectedStory().title()+"'"), "Share on Twitter", "width=550,height=420,scrollbars=no");
        }
        if (service == 'facebook') {
            window.open ("https://www.facebook.com/sharer/sharer.php?u="+encodeURIComponent(location.href), "Share on Facebook", "width=550,height=420,scrollbars=no");
        }
        $('.sharepanel').toggle();
    };
};

function App(){
    var self = this;

    self.twoColumnPlayer = ko.observable(false);

    // Layout
    self.resizeHandler = function() {
        var timeout;
        clearTimeout(timeout);
        timeout = setTimeout(function(){
            // $('#toolbarfiller').height($('#toolbars').height()+10);

            extraHeight = 0;
            if ($(window).width() < 1003 || (window.orientation == 0 || window.orientation == 180)) {
                $('.container').css('maxWidth', '671px');
                $('#toolbars').css('maxWidth', '664px');
                $('#toolbar_last').css('clear', 'both');
                $('#player').css('width', '657px');
                $('#genericpanel').css('width', '657px');
                self.twoColumnPlayer(false);
                extraHeight = 33;
            } else if ($(window).width() < 1335 || (window.orientation == 90 || window.orientation == -90)) {
                $('.container').css('maxWidth', '1003px');
                $('#toolbars').css('maxWidth', '996px');
                $('#toolbar_last').css('clear', 'none');
                $('#player').css('width', '989px');
                $('#genericpanel').css('width', '989px');
                self.twoColumnPlayer(true);
            } else {
                $('.container').css('maxWidth', '1335px');
                $('#toolbars').css('maxWidth', '1328px');
                $('#toolbar_last').css('clear', 'none');
                $('#player').css('width', '989px');
                $('#genericpanel').css('width', '989px');
                self.twoColumnPlayer(true);
            }

            if (self.loggedIn()) {
                $('#toolbarfiller').height(109+extraHeight);
                if ($(window).width() < 1003 || $(window).width() >= 1335) {
                    $('#toolbar_empty').show();
                } else {
                    $('#toolbar_empty').hide();
                }
            } else {
                $('#toolbarfiller').height(76);
                $('#toolbar_empty').hide();
            }
            // $('#player').height($(window).height()-$('#toolbars').height()-100);
            // $('.playercontent').height($('#player').height()-97);
        }, 100);
    };
    self.resizeHandler();
    $('.content').css('minHeight', $('.container').height()-$('.banner').height()-20);
    $(window).bind('resize', self.resizeHandler);

    $(window).scroll(function(e){ 
          $el = $('#toolbars'); 
          if ($(this).scrollTop() > $('.banner').height() + 20 && $el.css('position') != 'fixed'){ 
            $('#toolbars').css({'position': 'fixed', 'top': '0px'}); 
            $('#player').css({'position': 'fixed', 'top': $('#toolbars').height()+7});
            $('#genericpanel').css({'position': 'fixed', 'top': $('#toolbars').height()+7});
        } else if ($(this).scrollTop() <= $('.banner').height() + 20 && $el.css('position') == 'fixed'){ 
            $('#toolbars').css({'position': 'static'});
            $('#player').css({'position': 'absolute', 'top': 'auto'});
            $('#genericpanel').css({'position': 'absolute', 'top': 'auto'});
          }
          if ($(this).scrollTop() ==  $(document).height() - $(window).height()) {
            self.storyList.loadNext();
        }
    });

    // Language
    self.translations = {};
    self.translation = ko.observable({name:'en', story:{}, toolbar:{}, searchpanel:{filter:{}}, userfilepanel:{filter:{}}, storypanel:{}, interactionpanel:{}});
    self.selectedLanguage = ko.observable('en');
    self.loadLanguage = function(value) {
        if (!self.translations[value]) {
            return $.getJSON('javascripts/language/'+value+'.js', function(result){
                self.translations[value] = result;
                self.translation(self.translations[value]);
            });
        } else {
            return self.translation(self.translations[value]);
        }
    };

    // Themes
    self.themes = ko.observableArray([]);
    self.defaultTheme = null;
    self.loadThemes = function(){
        return $.get(apiUrl+"/awareness/Themes/list").then(function(result){
            var defer = $.Deferred(),
                requests = [];
            for (var i = result.values.length - 1; i >= 0; i--) {
                // self.themes.unshift(result.values[i]);
                requests.push(
                    $.get(apiUrl+"/awareness/Themes/"+result.values[i].id).then(function(result){
                        if (result.defaultTheme) {
                            self.defaultTheme = self.themes().length;
                        }
                        self.themes.push(result);
                    })
                );
            }
            $.when.apply($, requests).then(function() {
                defer.resolve();
            });
            return defer.promise();
        });
    };
    self.selectedTheme = ko.observable();
    self.previousTheme = null;
    self.selectedTheme.subscribe(function(value) {
        if (typeof value == 'object')
            self.previousTheme = value;
    }, this, "beforeChange");
    self.selectedTheme.subscribe(function(value) {
        if (!value || self.previousTheme == value) {
            return;
        }
        if (self.didLoad()) {
            self.previousTheme = value;
            self.storyList.reloadStories();
        }
        // console.log('theme = '+value.title);
        $('.background img').attr('src', '../images/'+value.wallpaper);
        $('.banner img').attr('src', '../images/'+value.banner);
    });

    // Stories
    self.storyList = new StoryList();
    $('#stories').masonry({
        itemSelector : '.storybox'
    });

    // Handle location hash
    self.didLoad = ko.observable(false);
    ko.computed(function() {
        if (!self.didLoad()) {
            return;
        }
        value = null;
        if (self.selectedLanguage()) {
            value = '/'+self.selectedLanguage();
        }
        if (self.selectedTheme()) {
            value += '/'+encodeURI(self.selectedTheme().title).replace(/%20/g, "+");
        }
        if (self.storyList.selectedStory()) {
            value += '/'+self.storyList.selectedStory().id;
        }
        if (value) {
            location.hash = value;
        }
    }, self).extend({ throttle: 1 });

    self.goHome = function(){
        self.storyList.clearSearch();
        self.storyBuilder.showStoryPanel(false);
        self.objectSearcher.showSearchPanel(false);
        self.storyList.previewObject(null);
        self.user().showUserFilePanel(false);
        self.storyList.selectedStory(null);
    };

    // Story panel
    self.storyBuilder = new StoryBuilder();

    // Files
    self.objectSearcher = new ObjectSearcher();

    // Panels
    self.selectedPanel = ko.observable(null);
    self.genericPanel = ko.computed(function(){
        if (self.selectedPanel() == 'contact') {
            return self.translation().contactpanel;
        }
        if (self.selectedPanel() == 'about') {
            return self.translation().aboutpanel;
        }
        if (self.selectedPanel() == 'help') {
            return self.translation().helppanel;
        }
        if (self.selectedPanel() == 'register') {
            self.showLoginPanel(false);
            return self.translation().registerpanel;
        }
        if (self.selectedPanel() == 'profile') {
            return self.translation().profilepanel;
        }
        return null;
    });

    // Display help on first load, then set cookie
    if (!$.cookie('firstLoad')) {
        self.selectedPanel('help');
        $.cookie('firstLoad', '1', { expires: 70 });
    }
    self.genericPanelScroll = function(direction) {
        if (!self.genericPanel().columns) {
            return;
        }
        if (direction > 0) {
            $('#scroller').scrollLeft(Math.min(
                $('#scroller').scrollLeft()+325, 
                $('#scroller div').width()
            ));
        }
        if (direction < 0) {
            $('#scroller').scrollLeft(Math.max(
                $('#scroller').scrollLeft()-325,
                0
            ));
        }
    };

    // User handling
    self.showLoginPanel = ko.observable(false);
    self.user = ko.observable(null);
    self.loggedIn = ko.observable(false);
    self.loggedIn.subscribe(function(value){
        if (value) {
            self.user().loadLibrary();
        }
    });
    self.login = function(formElement) {
        $.ajax(apiUrl+"/awareness/Users/login", {
            contentType: 'application/json',
            // dataType : 'json',
            type: "POST",
            data: ko.toJSON({email:$('#login_email').val(), password:$('#login_password').val()}),
            success: function(result){
                self.user(new User(result));
                self.loggedIn(true);
                // resize to make room for new toolbars
                self.resizeHandler();
                self.showLoginPanel(false);
                self.selectedPanel(null);
            },
            error: function(jqXHR, textStatus, errorThrown){
                alert(jqXHR.responseText);
            }
        });
    };
    self.logout = function() {
        $.get(apiUrl+"/awareness/session/logout");
        self.loggedIn(false);
        self.user(null);

        // resize to clear new toolbars
        self.resizeHandler();
    };
    self.register = function(){
        if (!$('#register_acceptterms').is(':checked')) {
            alert(a.translation().errors.invalidterms);
            return;
        }
        data = {
            id: null,
            username: $('#register_username').val(), 
            email: $('#register_email').val(),
            role: "contributor"
        };
        user = new User(data);
        $.when(user.save($('#register_password').val(), $('#register_passwordconfirmation').val())).then(function(){
            self.user(user);
            self.loggedIn(true);
            // resize to make room for new toolbars
            self.resizeHandler();
            self.selectedPanel(null);
        });
    };
    self.profile = function(){
        $.when(self.user().save($('#profile_password').val(), $('#profile_passwordconfirmation').val())).then(function(){
            self.selectedPanel(null);
        });
    };

    ko.computed(function(){
        if (self.storyList.selectedStory()) {
            if (self.loggedIn()) {
                self.user().showUserFilePanel(false);
            }
            self.storyBuilder.showStoryPanel(false);
            self.objectSearcher.showSearchPanel(false);
            self.storyList.previewObject(null);
        }
    }, self);

    // RUN
    self.loadLanguage('en');

    $.when(self.loadThemes()).then(function(){
        Path.map("#/:language/:theme").to(function(){
            // console.log('routing '+location.hash);
            self.loadLanguage(this.params['language']);
            self.selectedLanguage(this.params['language']);

            var theme = decodeURI(this.params['theme']).replace(/\+/g, " ");
            self.selectedTheme(ko.utils.arrayFirst(self.themes(), function(item){
                return item.title == theme;
            }));
        });
        Path.map("#/:language/:theme/:story").to(function(){
            self.loadLanguage(this.params['language']);
            self.selectedLanguage(this.params['language']);

            var theme = decodeURI(this.params['theme']).replace(/\+/g, " ");
            self.selectedTheme(ko.utils.arrayFirst(self.themes(), function(item){
                return item.title == theme;
            }));
            self.storyList.selectStoryById(this.params['story']);
        });

        $.ajax({url: apiUrl+"/awareness/session/user", contentType: 'application/json', dataType : 'json'}).then(function(result){
            self.user(new User(result));
            self.loggedIn(true);
            // resize to make room for new toolbars
            self.resizeHandler();
            self.showLoginPanel(false);
        });

        $('.loader').fadeOut();
        $.when(self.storyList.searchStories()).always(function(){
            self.didLoad(true);
            self.resizeHandler();

            if (self.defaultTheme) {
                Path.root("#/en/"+self.themes()[self.defaultTheme].title);
            } else {
                Path.root("#/en/");
            }
            Path.listen();
        });
    }, function(){
        alert(a.translation().errors.themeloadfailed);
    });
};

ko.observableArray.fn.sortBy = function(prop) {
    this.sort(function(obj1, obj2) {
        if (obj1[prop] < obj2[prop]) 
            return 1;
        if (obj1[prop] > obj2[prop])
            return -1;
        return 0;
    });
};

(function () {
    var _dragged, _noOp = function(){};
    ko.bindingHandlers.drag = {
        init: function(element, valueAccessor, allBindingsAccessor) {
            var dragOptions = valueAccessor().dragOptions || {};
            dragOptions.appendTo = 'body';
            dragOptions.helper = dragOptions.helper || 'clone';
            dragOptions.snapMode = 'inner';
            dragOptions.revert = 'invalid';
            dragOptions.revertDuration = 200;
            dragOptions.zIndex = 25000;
            dragOptions.start = function(){
                _dragged = ko.utils.unwrapObservable(valueAccessor().value);

                if (_dragged instanceof ForeignObject || _dragged instanceof StoryObject) {
                    a.storyList.previewObject(null);
                }
            };
            $(element).draggable(dragOptions).disableSelection();
        }    
    };

    ko.bindingHandlers.drop = {
        init: function(element, valueAccessor, allBindingsAccessor) {
            var dropOptions = valueAccessor().dropOptions || {};
            dropOptions.drop = function(event, ui) {
                if (_dragged == null) {
                    return;
                }
                if (typeof valueAccessor().objectIndex !== 'undefined') {
                    valueAccessor().value(_dragged, valueAccessor().objectIndex, event);
                } else {
                    valueAccessor().value(_dragged, event);
                }
                _dragged = null;
            };
            $(element).droppable(dropOptions);
        }    
    };

    ko.observableArray.fn.pushAll = function(valuesToPush) {
        var underlyingArray = this();
        this.valueWillMutate();
        ko.utils.arrayPushAll(underlyingArray, valuesToPush);
        this.valueHasMutated();
        return this;
    };

})();

function createUploader(){
    var uploader = new qq.FileUploader({
        element: document.getElementById('uploadImage'),
        action: apiUrl+'/awareness/FileReader/save',
        allowedExtensions: ['jpg', 'jpeg', 'png', 'gif'],
        sizeLimit: 4194304, /*4 MB*/
        debug: true
        // uploadButton: a.translation().userfilepanel.selectUpload
    });
};


$(function(){
    a = new App();
    ko.applyBindings(a);
});
