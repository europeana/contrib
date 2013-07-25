$(function(){
	/*check user credentials*/
	
   
	 $.ajax({
         contentType : "application/json",
         dataType : 'json',
         type : "GET",
         url : apiUrl+"session/user?"+new Date().getTime(),
         success : function(data) {                
             
             if(data==null || data.role!="admin"){
         		location.href = consoleUrl+"login.html";
         	}
             var a = '_id', b = '$oid';             
             
             user.uid=data[a][b];
             
             user.usename=data.username;
             user.role=data.role;
             
             user.email=data.email;
             // response
         },
         error : function(xhr) {
         	console.log(xhr);
        	location.href = consoleUrl+"login.html";
			return false;
         }
     });
    
	//$('input').checkBox();
	$('#toggle-all').click(function(){
 	$('#toggle-all').toggleClass('toggle-checked');
	//$('#mainform input[type=checkbox]').checkBox('toggle');
	return false;
	});
	
	$("#logout").click(function (){
		
		 $.ajax({
	         contentType : "application/json",
	         dataType : 'json',
	         type : "GET",
	         url : apiUrl+"session/logout",
	         success : function(data) {                
	             user=null; 
	             location.href = consoleUrl+"login.html";
	         	  // response
	         },
	         error : function(xhr) {
	         	console.log(xhr);
	        	location.href = consoleUrl+"login.html";
				return false;
	         }
	     });
	});
	
	$("#acc-settings").click(function(e){
		Accountsearch(user.email);
		/*	
	$('#UserTableContainer').jtable('updateRecord', {
		    record: {
		        id: user.uid,
		        username: user.username,
		        email: user.email,
		        accountActive: true,
		        role: user.role		        
		    }
		});
		});*/
	});
	
	
	 $("#adminhome").click(function(e){
		 e.preventDefault(); // if desired...
	    $('#page-heading').html('<h1>Dashboard<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').load('home.html');
		
		$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(0).attr('class','current');
		return false;
	 });
	
	 $(document).on("click", "#homeaddtheme",  function(){
		 addTheme();
	 });
	 
	 function addTheme(){
		 
		    $('#page-heading').html('<h1>Add theme<h1>');
			$('#contentdiv').html('');
			
			$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(2).attr('class','current');
		   $("#contentdiv").load("upload.html", function() {
			 	
			   $('#fileupload').fileupload({
			        dataType: 'json',
			        url: apiUrl+ 'FileReader/upload',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	$('#themefields :input[name=wallpaper]').val(result.files[0].dspname);
			            	$('#themefields :input[name=wallpaperName]').val(result.files[0].name);})
			            return false;
			        }
			    });
			   $('#fileupload').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   
			  
			   
			   $('#fileupload').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=wallpaper]').val("");
			        $('#themefields :input[name=wallpaperName]').val("");
			    });
			   
			  

			   $('#fileupload2').fileupload({
			        dataType: 'json',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        url: apiUrl+ 'FileReader/upload',  
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	
			            	$('#themefields :input[name=banner]').val(result.files[0].dspname);
			            	$('#themefields :input[name=bannerName]').val(result.files[0].name);})
			            return false;
			        }
			    });
			   $('#fileupload2').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   $('#fileupload2').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=banner]').val("");
			        $('#themefields :input[name=bannerName]').val("");
			    });

			   $('#fileupload3').fileupload({
			        dataType: 'json',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        url: apiUrl+ 'FileReader/upload',  
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	$('#themefields :input[name=minibanner]').val(result.files[0].dspname);
			            	$('#themefields :input[name=minibannerName]').val(result.files[0].name);})
			            	return false;
			        }
			    });
			   $('#fileupload3').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   $('#fileupload3').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=minibanner]').val("");
			        $('#themefields :input[name=minibannerName]').val("");
			    });			    
			    // Initialize the Image Gallery widget:
			    $('#fileupload .files').imagegallery();

			    $('#fileupload2 .files').imagegallery();
			    

			    $('#fileupload3 .files').imagegallery();
			    var myPicker = new jscolor.color(document.getElementById('themecolor'), {});
			    //new jscolor.color($('#themefields :input[name=themecolor]'), {});
			});
			 
 
	 }
	 
	 function editTheme(themeid){
		 
		    $('#page-heading').html('<h1>Edit theme<h1>');
			$('#contentdiv').html('');
			
			$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(2).attr('class','current');
		   $("#contentdiv").load("edittheme.html", function() {
			  wallurl="";
			  bannerurl="";
			  minibannerurl="";
			  $('#themefields :input[name=themeid]').val(themeid);
			   
	    	   
			   
			   
			   
			   $('#fileupload').fileupload({
			        dataType: 'json',
			        url: apiUrl+ 'FileReader/upload',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	$('#themefields :input[name=wallpaper]').val(result.files[0].dspname);
			            	$('#themefields :input[name=wallpaperName]').val(result.files[0].name);})
			            return false;
			        }
			    });
			   $('#fileupload').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   $('#fileupload').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=wallpaper]').val("");
			        $('#themefields :input[name=wallpaperName]').val("");
			    });
			   
			   

			   $('#fileupload2').fileupload({
			        dataType: 'json',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        url: apiUrl+ 'FileReader/upload',  
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	
			            	$('#themefields :input[name=banner]').val(result.files[0].dspname);
			            	$('#themefields :input[name=bannerName]').val(result.files[0].name);})
			            return false;
			        }
			    });
			   $('#fileupload2').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   $('#fileupload2').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=banner]').val("");
			        $('#themefields :input[name=bannerName]').val("");
			    });

			   $('#fileupload3').fileupload({
			        dataType: 'json',
			        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
			        url: apiUrl+ 'FileReader/upload',  
			        add: function (e, data) {
			            //data.submit();
			            var jqXHR = data.submit()
			            .success(function (result, textStatus, jqXHR) {
			            	$('#themefields :input[name=minibanner]').val(result.files[0].dspname);
			            	$('#themefields :input[name=minibannerName]').val(result.files[0].name);})
			            	return false;
			        }
			    });
			   $('#fileupload3').fileupload(
			    	    'option',
			    	    {
			    	        maxNumberOfFiles: 1,
			    	        sequentialUploads: true
			    	    }
			    	);
			   
			   $('#fileupload3').bind('fileuploaddestroy', function (e, data) {
			        $('#themefields :input[name=minibanner]').val("");
			        $('#themefields :input[name=minibannerName]').val("");
			    });
			   ShowProgressAnimation();
			   $.ajax({
	    		   contentType : "application/json",
	               type:'GET',
	               url:apiUrl+'Admin/theme/'+themeid+"?"+new Date().getTime(),
	               dataType:'json',
	               success:function (d) {
	            	   wallurl=apiUrl+"FileReader/uploadTheme?id="+d.wallpaper;
	            	   
	            	   bannerurl=apiUrl+"FileReader/uploadTheme?id="+d.banner;
	            	   minibannerurl=apiUrl+"FileReader/uploadTheme?id="+d.minibanner;
	            	   $('#themefields :input[name=title]').val(d.title);
	            	   $('textarea[name=description]').val(d.description);
	            	   $('#themefields :input[name=themecolor]').val(d.background);
	            	   $('#themefields :input[name=wallpaper]').val(d.wallpaper);
		               $('#themefields :input[name=wallpaperName]').val(d.wallpaperName);
		               $('#themefields :input[name=banner]').val(d.banner);
		               $('#themefields :input[name=bannerName]').val(d.bannerName);
		               $('#themefields :input[name=minibanner]').val(d.minibanner);
		               $('#themefields :input[name= minibannerName]').val(d.minibannerName);
		               $('#themefields :input[name=originalwallpaper]').val(d.wallpaper);
		               $('#themefields :input[name=originalbanner]').val(d.banner);
		               $('#themefields :input[name=originalminibanner]').val(d.minibanner);
		               var myPicker = new jscolor.color(document.getElementById('themecolor'), {});
					   myPicker.fromString(d.background);
		               $.ajax({
				            url: wallurl,
				            dataType: 'json',
				            type: 'GET',
				            context: $('#fileupload')[0]
				        }).done(function (result) {
				        	HideProgressAnimation();
				            $(this).fileupload('option', 'done')
				                .call(this, null, {result: result});
				        }).fail(function(jqXHR,textStatus,errorThrown) {
				        	HideProgressAnimation();
				        	alert( "Problem getting wallpaper file " );
				        });
		               $.ajax({
				            url: bannerurl,
				            dataType: 'json',
				            type: 'GET',
				            context: $('#fileupload2')[0]
				        }).done(function (result) {
				        	$(this).fileupload('option', 'done')
				                .call(this, null, {result: result});
				        }).fail(function(jqXHR, textStatus) {
				        	HideProgressAnimation();
				        	alert("Problem getting banner file " );
				        });  
		               $.ajax({
				            url: minibannerurl,
				            dataType: 'json',
				            type: 'GET',
				            context: $('#fileupload3')[0]
				        }).done(function (result) {
				        	$(this).fileupload('option', 'done')
				                .call(this, null, {result: result});
				        }).fail(function(jqXHR, textStatus) {
				        	HideProgressAnimation();
				        	alert( "Problem getting minibanner file" );
				        });  
	            	     
	            	   return true;
	            	   
	               },
	               error : function(xhr) {
	            	   HideProgressAnimation();
	            	   showDialg(xhr.responseText);
	            	   return false;
	                    
	            }
	           });	   
			   
			    // Initialize the Image Gallery widget:
			    $('#fileupload .files').imagegallery();

			    $('#fileupload2 .files').imagegallery();
			    

			    $('#fileupload3 .files').imagegallery();
			    
			});
			 

	 }
	 
	 
	 $(document).on("click", "#themeedit_reset",  function(){
		   ///$("#theme_reset").delegate("click", function(event){
			 
		     $('#themefields :input[name=title]').val("");
		     $('textarea[name=description]').val("");
		     $('#themefields :input[name=themecolor]').val("");
		     
			 $('#fileupload table tbody tr.template-download td.delete button').click();
			 $('#fileupload2 table tbody tr.template-download td.delete button').click();
			 $('#fileupload3 table tbody tr.template-download td.delete button').click();
		 });
	 
	 $(document).on("click", "#theme_reset",  function(){
	   ///$("#theme_reset").delegate("click", function(event){
		 
		 $('#themefields')[0].reset();
		 $('#fileupload table tbody tr.template-download td.delete button').click();
		 $('#fileupload2 table tbody tr.template-download td.delete button').click();
		 $('#fileupload3 table tbody tr.template-download td.delete button').click();
	 });
	 
	 $(document).on("click", "#theme_submit",  function(){
		 if($('#themefields :input[name=title]').val().trim().length ==0){
     		showDialg("Title cannot be empty!");
     		return false;
     	  
     	}
		 if($('textarea[name=description]').val().trim().length ==0){
     		showDialg("Description cannot be empty!");
     		return false;
     	  
     	}
		 if($('#themefields :input[name=wallpaper]').val().trim().length ==0){
	     		showDialg("Theme wallpaper cannot be empty!");
	     		return false;
	     	  
	     	}
		 if($('#themefields :input[name=banner]').val().trim().length ==0){
	     		showDialg("Theme banner cannot be empty!");
	     		return false;
	     	  
	     	}
		 if($('#themefields :input[name=minibanner]').val().trim().length ==0){
	     		showDialg("Mini banner cannot be empty!");
	     		return false;
	     	  
	     	}
		 ShowProgressAnimation();
     		    	   $.ajax({
		    		   contentType : "application/json",
		               type:'POST',
		               url:apiUrl+'Themes/save',
		               data: '{'+'"title":'+ '"'+$('#themefields :input[name=title]').val()+'"'+
		            	   ',"description":'+'"'+$('textarea[name=description]').val()+'"'+ 
		            	   ',"wallpaper":'+'"'+$('#themefields :input[name=wallpaper]').val()+'"'+
		            	   ',"wallpaperName":'+'"'+$('#themefields :input[name=wallpaperName]').val()+'"'+
		            	   ',"banner":'+'"'+$('#themefields :input[name=banner]').val()+'"'+
		            	   ',"bannerName":'+'"'+$('#themefields :input[name=bannerName]').val()+'"'+
		            	   ',"minibanner":'+'"'+$('#themefields :input[name=minibanner]').val()+'"'+
		            	   ',"minibannerName":'+'"'+$('#themefields :input[name=minibannerName]').val()+'"'+
		            	   ',"background":'+'"'+$('#themefields :input[name=themecolor]').val()+'"'+
		            	   ',"defaultTheme":'+false+'}',
		               dataType:'json',
		                success:function (d) {
		                	HideProgressAnimation();
		                	$('#themeshow').trigger('click');
		                   
		                   
		               },
		               error : function(xhr) {
		            	   HideProgressAnimation();
		            	   var $dialog = $('<div></div>')
			   				.html(xhr.responseText)
			   				.dialog({
			   					autoOpen: false,
			   					title: 'Theme save failure',
			   					buttons: {
			   						Ok: function() {
			   							$( this ).dialog( "close" );
			   						}
			   					}
			   				});
			               	$dialog.dialog('open');
			               	return false;
		                    
		            }
		           });	   
		    	   
		      
		  
		
	 });
	 
	 
	 $(document).on("click", "#themeedit_submit",  function(){
		 if($('#themefields :input[name=title]').val().trim().length ==0){
     		showDialg("Title cannot be empty!");
     		return false;
     	  
     	}
		 if($('textarea[name=description]').val().trim().length ==0){
     		showDialg("Description cannot be empty!");
     		return false;
     	  
     	}
		 if($('#themefields :input[name=wallpaper]').val().trim().length ==0){
	     		showDialg("Theme wallpaper cannot be empty!");
	     		return false;
	     	  
	     	}
		 if($('#themefields :input[name=banner]').val().trim().length ==0){
	     		showDialg("Theme banner cannot be empty!");
	     		return false;
	     	  
	     	}
		 if($('#themefields :input[name=minibanner]').val().trim().length ==0){
	     		showDialg("Mini banner cannot be empty!");
	     		return false;
	     	  
	     	}
		 ShowProgressAnimation();
     		    	   $.ajax({
		    		   contentType : "application/json",
		               type:'POST',
		               url:apiUrl+'Admin/themes/update',
		               data: '{'+'"id":'+ '"'+$('#themefields :input[name=themeid]').val()+'"'+
		                   ',"title":'+ '"'+$('#themefields :input[name=title]').val()+'"'+
		            	   ',"description":'+'"'+$('textarea[name=description]').val()+'"'+ 
		            	   ',"wallpaper":'+'"'+$('#themefields :input[name=wallpaper]').val()+'"'+
		            	   ',"originalwallpaper":'+'"'+$('#themefields :input[name=originalwallpaper]').val()+'"'+
		            	   ',"wallpaperName":'+'"'+$('#themefields :input[name=wallpaperName]').val()+'"'+
		            	   ',"banner":'+'"'+$('#themefields :input[name=banner]').val()+'"'+
		            	   ',"originalbanner":'+'"'+$('#themefields :input[name=originalbanner]').val()+'"'+
		            	   ',"bannerName":'+'"'+$('#themefields :input[name=bannerName]').val()+'"'+
		            	   ',"minibanner":'+'"'+$('#themefields :input[name=minibanner]').val()+'"'+
		            	   ',"originalminibanner":'+'"'+$('#themefields :input[name=originalminibanner]').val()+'"'+
		            	   ',"minibannerName":'+'"'+$('#themefields :input[name=minibannerName]').val()+'"'+
		            	   ',"background":'+'"'+$('#themefields :input[name=themecolor]').val()+'"'+
		            	   ',"defaultTheme":'+false+'}',
		               dataType:'json',
		                success:function (d) {
		                	HideProgressAnimation();
		                	$('#themeshow').trigger('click');
		                   
		                   
		               },
		               error : function(xhr) {
		            	   HideProgressAnimation();
		            	   var $dialog = $('<div></div>')
			   				.html(xhr.responseText)
			   				.dialog({
			   					autoOpen: false,
			   					title: 'Theme save failure',
			   					buttons: {
			   						Ok: function() {
			   							$( this ).dialog( "close" );
			   						}
			   					}
			   				});
			               	$dialog.dialog('open');
			               	return false;
		                    
		            }
		           });	   
		    	   
		      
		  
		
	 });
	 
	 
	 $("#themeshow").click(function(e){
		 			 
			 
		  e.preventDefault(); // if desired...
	      $('#page-heading').html('<h1>Theme list<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').append('<div id="ThemeTableContainer"></div>');
		
		$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(2).attr('class','current');
		/*create the table*/
		 $('#ThemeTableContainer').jtable({
			 contentType : "application/json",
	         dataType : 'json',
	         type: 'POST',
	            title: '&nbsp;',
	            paging: true, //Enable paging
	            pageSize: 10, //Set page size (default: 10)
	            sorting: true, //Enable sorting
	            defaultSorting: 'title', //Set default sorting
	            deleteConfirmation: function(data) {
	                data.deleteConfirmMessage = 'Are you sure you want to delete this theme? Themes that contain stories can not be deleted.';
	            },
	            toolbar: {
	                items: [{
	                    text: '+ Add new Theme',
	                    click: function () {
	                        addTheme();
	                    }
	                }]
	            },
	            actions: {
	                listAction: apiUrl+'Admin/themes',
	                deleteAction: apiUrl+'Admin/themes/delete'
	            },
	            fields: {
	                id: {
	                    key: true,
	                    list: false
	                },
	                title: {
	                    title: 'Title',
	                    width: '30%',
	                    input: function (data) {
	                            if (data.record) {
	                                return '<input type="text" name="title" style="width:300px" value="' + data.record.title + '"/>';
	                            } else {
	                                return '<input type="text" name="title" style="width:300px"  />';
	                            }
	                        },
	                    list: true
	                },
	                description: {
	                    title: 'Description',
	                    width: '50%',
	                    type:  'textarea',
                        list: true,
                        sorting: false
	                },
	                
	                stories: {
	                    title: 'Stories',
	                    sorting: false,
	                    width: "5%",
	                    display: function (data) {
	                    	var $totals = $('<span></span>');
	                    	 $.ajax({
	                             contentType : "application/json",
	                             dataType : 'json',
	                             type : "GET",
	                             url : apiUrl+"Admin/Themes/"+data.record.id+"/totalstories"+"?"+new Date().getTime(),
	                             success : function(data) {      
	                            	 
	                                 $totals.text(data.totalSize); 
	                                  // response
	                                 return $totals;
	                             },
	                             error : function(xhr) {
	                             	console.log(xhr);
	                            	return false;
	                             }
	                         });
	                    	
	                        return $totals;
	                    }
	                },
	                previewcol: {
	                	width: "5%",
	                    display: function (data) {
	                        var $link =$('<a href="'+apiUrl+'html/dspconsole/previewtheme.html?'+data.record.id+'">Preview</a>');
	                        $link.click(function() {
	                            window.open($(this).attr('href'));
	                            return false;
	                        });
	                        return $link;
	                    },
	                    sorting: false
	                    
	                },
	                editcol: {
	                	width:"5%",
	                	display: function (data){
	                		
	                		var $button =$('<button class="jtable-command-button jtable-edit-command-button" title="Edit Theme"><span>Edit Theme</span></button>');
	                		$button.click(function(){ editTheme(data.record.id) });
	                        return $button;
	                	},
	                    sorting: false
	                    
	                }
	                
	            }
	        });
		 $('#ThemeTableContainer').jtable('load');
		 return false;
	    });
	 
	 $("#searchbtn").click(function(e){
		  selopt = $('select[name="searchopt"]').val();
		   if(selopt=="Users"){
			   Usersearch();
		   }
		   if(selopt=="Themes"){
			   Themesearch();
		   }
		   
	 });
	 
	 function Themesearch(){
			 
		
	     $('#page-heading').html('<h1>Theme search<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').append('<div id="ThemeTableContainer"></div>');
		searchval=$(document).find('#searchterm').val();
		$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(2).attr('class','current');
		/*create the table*/
		 $('#ThemeTableContainer').jtable({
			 contentType : "application/json",
	         dataType : 'json',
	         type: 'POST',
	            title: '&nbsp;',
	            paging: true, //Enable paging
	            pageSize: 10, //Set page size (default: 10)
	            sorting: true, //Enable sorting
	            defaultSorting: 'title', //Set default sorting
	            deleteConfirmation: function(data) {
	                data.deleteConfirmMessage = 'Are you sure you want to delete this theme? Themes that contain stories can not be deleted.';
	            },
	            toolbar: {
	                items: [{
	                    text: '+ Add new Theme',
	                    click: function () {
	                        addTheme();
	                    }
	                }]
	            },
	            actions: {
	                listAction: apiUrl+'Admin/themes/search?searchterm='+searchval,
	                deleteAction: apiUrl+'Admin/themes/delete'
	            },
	            fields: {
	                id: {
	                    key: true,
	                    list: false
	                },
	                title: {
	                    title: 'Title',
	                    width: '30%',
	                    input: function (data) {
	                            if (data.record) {
	                                return '<input type="text" name="title" style="width:300px" value="' + data.record.title + '"/>';
	                            } else {
	                                return '<input type="text" name="title" style="width:300px"  />';
	                            }
	                        },
	                    list: true
	                },
	                description: {
	                    title: 'Description',
	                    width: '50%',
	                    type:  'textarea',
                       list: true,
                       sorting: false
	                },
	                
	                stories: {
	                    title: 'Stories',
	                    sorting: false,
	                    width: "5%",
	                    display: function (data) {
	                    	var $totals = $('<span></span>');
	                    	 $.ajax({
	                             contentType : "application/json",
	                             dataType : 'json',
	                             type : "GET",
	                             url : apiUrl+"Admin/Themes/"+data.record.id+"/totalstories"+"?"+new Date().getTime(),
	                             success : function(data) {      
	                            	 
	                                 $totals.text(data.totalSize); 
	                                  // response
	                                 return $totals;
	                             },
	                             error : function(xhr) {
	                             	console.log(xhr);
	                            	return false;
	                             }
	                         });
	                    	
	                        return $totals;
	                    }
	                },
	                previewcol: {
	                	width: "5%",
	                    display: function (data) {
	                        var $link =$('<a href="'+apiUrl+'html/dspconsole/previewtheme.html?'+data.record.id+'">Preview</a>');
	                        $link.click(function() {
	                            window.open($(this).attr('href'));
	                            return false;
	                        });
	                        return $link;

	                    },
	                    sorting: false
	                    
	                },
	                editcol: {
	                	width:"5%",
	                	display: function (data){
	                		
	                		var $button =$('<button class="jtable-command-button jtable-edit-command-button" title="Edit Theme"><span>Edit Theme</span></button>');
	                		$button.click(function(){ editTheme(data.record.id) });
	                        return $button;
	                	},
	                    sorting: false
	                    
	                }
	                
	            }
	        });
		 $('#ThemeTableContainer').jtable('load');
		 return false;
	    }
	 
	 
	 $("#usershow").click(function(e){
	      e.preventDefault(); // if desired...
	      $('#page-heading').html('<h1>User list<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').append('<div id="UserTableContainer"></div>');
		
		$('div.nav > div.table > ul').removeClass('current').addClass('select');$('div.nav > div.table').children('ul').eq(1).attr('class','current');
		/*create the table*/
		 $('#UserTableContainer').jtable({
			 contentType : "application/json",
	         dataType : 'json',
	         type: 'POST',
	            title: '&nbsp;',
	            paging: true, //Enable paging
	            pageSize: 10, //Set page size (default: 10)
	            sorting: true, //Enable sorting
	            defaultSorting: 'accountCreated DESC', //Set default sorting
	            deleteConfirmation: function(data) {
	                data.deleteConfirmMessage = 'Are you sure you want to delete this user? Deleteting him will also delete all his stories and story objects.';
	            },
	            actions: {
	                listAction: apiUrl+'Admin/users',
	                createAction: apiUrl+'Admin/users/save',
	                updateAction: apiUrl+'Admin/users/update',
	                deleteAction: apiUrl+'Admin/users/delete'
	            },
	            fields: {
	                id: {
	                    key: true,
	                    list: false
	                },
	                username: {
	                    title: 'Username',
	                    width: '20%',
	                    input: function (data) {
	                            if (data.record) {
	                                return '<input type="text" name="username" style="width:200px" value="' + data.record.username + '"/>';
	                            } else {
	                                return '<input type="text" name="username" style="width:200px"  />';
	                            }
	                        },
	                    list: true
	                },
	                email: {
	                    title: 'Email',
	                    width: '25%',
	                    input: function (data) {
                            if (data.record) {
                                return '<input type="text" name="email" style="width:200px" value="' + data.record.email + '"/>';
                            } else {
                                return '<input type="text" name="email" style="width:200px"  />';
                            }
                        },
                    list: true
	                },
	                accountActive: {
	                    title: 'Status',
	                    width: '15%',
	                    type: 'combobox',
	                    options: [
                        { Value: false, DisplayText: 'Disabled' },
                        { Value: true, DisplayText: 'Active' }
                        
                    	]
	                },
	                role: {
	                    title: 'User role',
	                    width: '15%',
	                    options: { 'contributor': 'Contributor', 'editor': 'Editor', 'admin': 'Admin' },
	                    list: true
	                }
	                ,
	                stories: {
	                    title: 'Stories',
	                    sorting: false,
	                    display: function (data) {
	                    	var $totals = $('<span></span>');
	                    	 $.ajax({
	                             contentType : "application/json",
	                             dataType : 'json',
	                             type : "GET",
	                             url : apiUrl+"Users/"+data.record.id+"/totalstories"+"?"+new Date().getTime(),
	                             success : function(data) {      
	                            	 
	                                 $totals.text(data.totalSize); 
	                                  // response
	                                 return $totals;
	                             },
	                             error : function(xhr) {
	                             	console.log(xhr);
	                            	return false;
	                             }
	                         });
	                    	
	                        return $totals;
	                    },
	                create:false,
	                edit:false
	                }
	                ,
	                accountCreated: {
	                    title: 'Account created',
	                    width: '30%',
	                    type: 'date',
	                    create: false,
	                    edit: false
	                },
	                password: {
	                    title: 'New Password',
	                    type: 'password',
	                    input: function (data) {
	                    	if (data.record) {
	                    	    $(this).title="Password";
	                    	}
                                return '<input type="password" name="password" style="width:200px" value=""/>';
                            
                        },
	                    list: false
	                },
	                passwordConf: {
	                    title: 'New Password confirmation',
	                    type: 'password',
	                    input: function (data) {
                            
                            return '<input type="password" name="passwordConf" style="width:200px" value=""/>';
                        
                        },
	                    list: false
	                }
	                
	            },
	            formCreated: function (event, data) 
	            {    
	            	if(data.formType=="create"){
	            	  $(data.form.find('input[name="password"]')).parent('div').prev('div').text("Password");
	            	  $(data.form.find('input[name="passwordConf"]')).parent('div').prev('div').text("Password confirmation");
	            	}
	            	else{
	            	 	

	            		$(data.form.find('input[name="password"]')).closest('div.jtable-input-field-container').before('<p>&nbsp;<p style="padding:10px;margin-bottom:15px;background:-moz-linear-gradient(center top , #EDEDED 0%, #C4C4C4 100%) repeat scroll 0 0 transparent;"><b>Reset password</b></p>');
	            	}
	            },
	            formSubmitting: function (event, data) 
	            {   
	            	if($(data.form.find('input[name="username"]')).val().trim().length ==0){
	            		showDialg("Username cannot be empty!");
	            		return false;
	            	  
	            	}
	            	if($(data.form.find('input[name="email"]')).val().trim().length ==0){
	            		showDialg("Email cannot be empty!");
	            		return false;
	            	  
	            	}
	            	
	            	
	            	if(data.formType=="create"){
		            	if($(data.form.find('input[name="password"]')).val().trim().length ==0){
		            		showDialg("Password cannot be empty!");
		            		return false;
		            	  
		            	}
		            	
		            	if($(data.form.find('input[name="passwordConf"]')).val().trim().length ==0){
		            		showDialg("Password confirmation cannot be empty!");
		            		return false;
		            	  
		            	}
		            	else{
		            		if($(data.form.find('input[name="passwordConf"]')).val()!=$(data.form.find('input[name="password"]')).val()){
			            		showDialg("Password and password confirmation must match!");
			            		return false;
			            	  
			            	}  		            	}
		                }
	              
		    		if(($(data.form.find('input[name="password"]')).val().trim().length>0) && ($(data.form.find('input[name="passwordConf"]')).val()!=$(data.form.find('input[name="password"]')).val())){
	            		showDialg("Password and password confirmation must match!");
	            		return false;
	            	  
	            	}  		            	
	    
		            var emailval=$(data.form.find('input[name="email"]')).val();
		            if(validateEmail(emailval)==false){
		            	showDialg("Please enter a valid email address");
	            		return false;
	            	  
		            }
		            if($(data.form.find('input[name="password"]')).val().trim().length >0 && $(data.form.find('input[name="password"]')).val().trim().length<6){
	            		showDialg("Password must be more than 5 characters long");
	            		return false;
	            	  
	            	}
	            
		 		}
	        });
		 $('#UserTableContainer').jtable('load');
		 return false;
	    });
	
	function Usersearch(searchword){
	     $('#page-heading').html('<h1>Search Users results<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').append('<div id="UserTableContainer"></div>');
		
		$('div.nav > div.table > ul').removeClass('current').addClass('select');
		/*create the table*/
		
		searchval=$(document).find('#searchterm').val();
		 $('#UserTableContainer').jtable({
			 contentType : "application/json",
	         dataType : 'json',
	         type: 'POST',
	            title: '&nbsp;',
	            paging: true, //Enable paging
	            pageSize: 10, //Set page size (default: 10)
	            sorting: true, //Enable sorting
	            defaultSorting: 'username', //Set default sorting
	            deleteConfirmation: function(data) {
	                data.deleteConfirmMessage = 'Are you sure you want to delete this user? Deleteting him will also delete all his stories and story objects.';
	            },
	            actions: {
	                listAction: apiUrl+'Admin/users/search?searchterm='+searchval,
	                createAction: apiUrl+'Admin/users/save',
	                updateAction: apiUrl+'Admin/users/update',
	                deleteAction: apiUrl+'Admin/users/delete'
	            },
	            fields: {
	                id: {
	                    key: true,
	                    list: false
	                },
	                username: {
	                    title: 'Username',
	                    width: '20%',
	                    input: function (data) {
	                            if (data.record) {
	                                return '<input type="text" name="username" style="width:200px" value="' + data.record.username + '"/>';
	                            } else {
	                                return '<input type="text" name="username" style="width:200px"  />';
	                            }
	                        },
	                    list: true
	                },
	                email: {
	                    title: 'Email',
	                    width: '25%',
	                    input: function (data) {
                           if (data.record) {
                               return '<input type="text" name="email" style="width:200px" value="' + data.record.email + '"/>';
                           } else {
                               return '<input type="text" name="email" style="width:200px"  />';
                           }
                       },
                   list: true
	                },
	                accountActive: {
	                    title: 'Status',
	                    width: '15%',
	                    type: 'combobox',
	                    options: [
                       { Value: false, DisplayText: 'Disabled' },
                       { Value: true, DisplayText: 'Active' }
                       
                   	]
	                },
	                role: {
	                    title: 'User role',
	                    width: '15%',
	                    options: { 'contributor': 'Contributor', 'editor': 'Editor', 'admin': 'Admin' },
	                    list: true
	                }
	                ,
	                stories: {
	                    title: 'Stories',
	                    sorting: false,
	                    display: function (data) {
	                    	var $totals = $('<span></span>');
	                    	 $.ajax({
	                             contentType : "application/json",
	                             dataType : 'json',
	                             type : "GET",
	                             url : apiUrl+"Users/"+data.record.id+"/totalstories"+"?"+new Date().getTime(),
	                             success : function(data) {      
	                            	 
	                                 $totals.text(data.totalSize); 
	                                  // response
	                                 return $totals;
	                             },
	                             error : function(xhr) {
	                             	console.log(xhr);
	                            	return false;
	                             }
	                         });
	                    	
	                        return $totals;
	                    },
	                create:false,
	                edit:false
	                },
	                accountCreated: {
	                    title: 'Account created',
	                    width: '30%',
	                    type: 'date',
	                    create: false,
	                    edit: false
	                },
	                password: {
	                    title: 'New Password',
	                    type: 'password',
	                    input: function (data) {
	                    	if (data.record) {
	                    	    $(this).title="Password";
	                    	}
                               return '<input type="password" name="password" style="width:200px" value=""/>';
                           
                       },
	                    list: false
	                },
	                passwordConf: {
	                    title: 'New Password confirmation',
	                    type: 'password',
	                    input: function (data) {
                           
                           return '<input type="password" name="passwordConf" style="width:200px" value=""/>';
                       
                       },
	                    list: false
	                }	                
	            },
	            formCreated: function (event, data) 
	            {    
	            	if(data.formType=="create"){
	            	  $(data.form.find('input[name="password"]')).parent('div').prev('div').text("Password");
	            	  $(data.form.find('input[name="passwordConf"]')).parent('div').prev('div').text("Password confirmation");
	            	}
	            	else{
	            	 	

	            		$(data.form.find('input[name="password"]')).closest('div.jtable-input-field-container').before('<p>&nbsp;<p style="padding:10px;margin-bottom:15px;background:-moz-linear-gradient(center top , #EDEDED 0%, #C4C4C4 100%) repeat scroll 0 0 transparent;"><b>Reset password</b></p>');
	            	}
	            },
	            formSubmitting: function (event, data) 
	            {   
	            	if($(data.form.find('input[name="username"]')).val().trim().length ==0){
	            		showDialg("Username cannot be empty!");
	            		return false;
	            	  
	            	}
	            	if($(data.form.find('input[name="email"]')).val().trim().length ==0){
	            		showDialg("Email cannot be empty!");
	            		return false;
	            	  
	            	}
	            	
	            	
	            	if(data.formType=="create"){
		            	if($(data.form.find('input[name="password"]')).val().trim().length ==0){
		            		showDialg("Password cannot be empty!");
		            		return false;
		            	  
		            	}
		            	
		            	if($(data.form.find('input[name="passwordConf"]')).val().trim().length ==0){
		            		showDialg("Password confirmation cannot be empty!");
		            		return false;
		            	  
		            	}
		            	else{
		            		if($(data.form.find('input[name="passwordConf"]')).val()!=$(data.form.find('input[name="password"]')).val()){
			            		showDialg("Password and password confirmation must match!");
			            		return false;
			            	  
			            	}  		            	}
		                }
	              
		    		if(($(data.form.find('input[name="password"]')).val().trim().length>0) && ($(data.form.find('input[name="passwordConf"]')).val()!=$(data.form.find('input[name="password"]')).val())){
	            		showDialg("Password and password confirmation must match!");
	            		return false;
	            	  
	            	}  		            	
	    
		            var emailval=$(data.form.find('input[name="email"]')).val();
		            if(validateEmail(emailval)==false){
		            	showDialg("Please enter a valid email address");
	            		return false;
	            	  
		            }
		            if($(data.form.find('input[name="password"]')).val().trim().length >0 && $(data.form.find('input[name="password"]')).val().trim().length<6){
	            		showDialg("Password must be more than 5 characters long");
	            		return false;
	            	  
	            	}
	            
		 		}
	        });
		 $('#UserTableContainer').jtable('load');
		 return false;
	    }
	
	function Accountsearch(searchval){
	     $('#page-heading').html('<h1>Account settings<h1>');
		$('#contentdiv').html('');
		$('#contentdiv').append('<div id="UserTableContainer"></div>');
		
		$('div.nav > div.table > ul').removeClass('current').addClass('select');
		/*create the table*/
		
		 $('#UserTableContainer').jtable({
			 contentType : "application/json",
	         dataType : 'json',
	         type: 'POST',
	            title: '&nbsp;',
	            paging: false, //Enable paging
	            actions: {
	                listAction: apiUrl+'Admin/users/search?searchterm='+searchval+'&jtPageSize=0&jtSorting=username ASC&jtStartIndex=0',
	                updateAction: apiUrl+'Admin/users/update',
	            },
	            fields: {
	                id: {
	                    key: true,
	                    list: false
	                },
	                username: {
	                    title: 'Username',
	                    width: '20%',
	                    input: function (data) {
	                            if (data.record) {
	                                return '<input type="text" name="username" style="width:200px" value="' + data.record.username + '"/>';
	                            } else {
	                                return '<input type="text" name="username" style="width:200px"  />';
	                            }
	                        },
	                    list: true
	                },
	                email: {
	                    title: 'Email',
	                    width: '25%',
	                    input: function (data) {
                          if (data.record) {
                              return '<input type="text" name="email" style="width:200px" value="' + data.record.email + '"/>';
                          } else {
                              return '<input type="text" name="email" style="width:200px"  />';
                          }
                      },
                  list: true
	                },
	                accountActive: {
	                    title: 'Status',
	                    width: '15%',
	                    type: 'combobox',
	                    options: [
                      { Value: false, DisplayText: 'Disabled' },
                      { Value: true, DisplayText: 'Active' }
                      
                  	]
	                },
	                role: {
	                    title: 'User role',
	                    width: '15%',
	                    options: { 'contributor': 'Contributor', 'editor': 'Editor', 'admin': 'Admin' },
	                    list: true
	                }
	                ,
	                stories: {
	                    title: 'Stories',
	                    sorting: false,
	                    display: function (data) {
	                    	var $totals = $('<span></span>');
	                    	 $.ajax({
	                             contentType : "application/json",
	                             dataType : 'json',
	                             type : "GET",
	                             url : apiUrl+"Users/"+data.record.id+"/totalstories"+"?"+new Date().getTime(),
	                             success : function(data) {      
	                            	 
	                                 $totals.text(data.totalSize); 
	                                  // response
	                                 return $totals;
	                             },
	                             error : function(xhr) {
	                             	console.log(xhr);
	                            	return false;
	                             }
	                         });
	                    	
	                        return $totals;
	                    },
	                edit:false
	                },
	                accountCreated: {
	                    title: 'Account created',
	                    width: '30%',
	                    type: 'date',
	                    create: false,
	                    edit: false
	                },
	                password: {
	                    title: 'New Password',
	                    type: 'password',
	                    input: function (data) {
	                    	if (data.record) {
	                    	    $(this).title="Password";
	                    	}
                              return '<input type="password" name="password" style="width:200px" value=""/>';
                          
                      },
	                    list: false
	                },
	                passwordConf: {
	                    title: 'New Password confirmation',
	                    type: 'password',
	                    input: function (data) {
                          
                          return '<input type="password" name="passwordConf" style="width:200px" value=""/>';
                      
                      },
	                    list: false
	                }	                
	            },
	            formCreated: function (event, data) 
	            {    
	            	

	            		$(data.form.find('input[name="password"]')).closest('div.jtable-input-field-container').before('<p>&nbsp;<p style="padding:10px;margin-bottom:15px;background:-moz-linear-gradient(center top , #EDEDED 0%, #C4C4C4 100%) repeat scroll 0 0 transparent;"><b>Reset password</b></p>');
	            	
	            },
	            formSubmitting: function (event, data) 
	            {   
	            	if($(data.form.find('input[name="username"]')).val().trim().length ==0){
	            		showDialg("Username cannot be empty!");
	            		return false;
	            	  
	            	}
	            	if($(data.form.find('input[name="email"]')).val().trim().length ==0){
	            		showDialg("Email cannot be empty!");
	            		return false;
	            	  
	            	}
	            	
	            	
	            	
	              
		    		if(($(data.form.find('input[name="password"]')).val().trim().length>0) && ($(data.form.find('input[name="passwordConf"]')).val()!=$(data.form.find('input[name="password"]')).val())){
	            		showDialg("Password and password confirmation must match!");
	            		return false;
	            	  
	            	}  		            	
	    
		            var emailval=$(data.form.find('input[name="email"]')).val();
		            if(validateEmail(emailval)==false){
		            	showDialg("Please enter a valid email address");
	            		return false;
	            	  
		            }
		            if($(data.form.find('input[name="password"]')).val().trim().length >0 && $(data.form.find('input[name="password"]')).val().trim().length<6){
	            		showDialg("Password must be more than 5 characters long");
	            		return false;
	            	  
	            	}
	            
		 		}
	        });
		 $('#UserTableContainer').jtable('load');
		 return false;
	    }
	 
	 function showDialg(message){
		 var $dialog = $('<div></div>')
			.html(message)
			.dialog({
				autoOpen: false,
				title: 'Save failure',
				buttons: {
					Ok: function() {
						$( this ).dialog( "close" );
					}
				}
			});
	 	$dialog.dialog('open');
	}
	 
	 function validateEmail(email) { 
		    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		    
		    return re.test(email);
		} 
	 
	 
	 
});
