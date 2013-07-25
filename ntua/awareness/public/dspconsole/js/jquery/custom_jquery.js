var apiUrl = "http://localhost:9000/awareness/";
var consoleUrl = "http://localhost:9000/awareness/html/dspconsole/";
user=new Object();

// 1 - START DROPDOWN SLIDER SCRIPTS ------------------------------------------------------------------------

$(document).ready(function () {
    $(".showhide-account").click(function () {
        $(".account-content").slideToggle("fast");
        $(this).toggleClass("active");
        return false;
    });
});

$(document).ready(function () {
    $(".action-slider").click(function () {
        $("#actions-box-slider").slideToggle("fast");
        $(this).toggleClass("activated");
        return false;
    });
});


$(document).ready(function () {
    $("#loading-div-background").css({ opacity: 0.8 });
    
});

function ShowProgressAnimation() {
   

    $("#loading-div-background").show();

} 

function HideProgressAnimation() {
	   

    $("#loading-div-background").hide();

} 


//  END ----------------------------- 1

// 2 - START LOGIN PAGE SHOW HIDE BETWEEN LOGIN AND FORGOT PASSWORD BOXES--------------------------------------

$(document).ready(function () {
	$("#submit_login").click(function (){
	    
    	var formData = form2js('user_signin', '.', true,
    			function(node)
    			{
    			if (node.id && node.id.match(/callbackTest/))
    			{
    			return { name: node.id, value: node.innerHTML };
    			}
    			});

    			
    	var dat=JSON.stringify(formData, null, '\t'); 
    
       $.ajax({
            contentType : "application/json",
            dataType : 'json',
            type : "POST",
            url : apiUrl+"Users/login",
            data : dat, //json serialization (like array.serializeArray() etc)
            success : function(data) {                
                
                user.usename=data.username;
                user.role=data.role;
                user.uid=data.uid;
                user.email=data.email;
                
                if(user.role=="admin"){
                	var newurl=consoleUrl+"dashboard.html";
                	
                	location.href = newurl;
                }else{
                	var $dialog = $('<div></div>')
    				.html("Unauthorized access")
    				.dialog({
    					autoOpen: false,
    					title: 'Login failure',
    					buttons: {
    						Ok: function() {
    							$( this ).dialog( "close" );
    						}
    					}
    				});
                	$dialog.dialog('open');
                	return false;
                }
               // response
            },
            error : function(xhr) {
            	var $dialog = $('<div></div>')
				.html(xhr.responseText)
				.dialog({
					autoOpen: false,
					title: 'Login failure',
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
        
    })
	
	$(".forgot-pwd").click(function () {
	$("#loginbox").hide();
	$("#forgotbox").show();
	return false;
	});

});

$(document).ready(function () {
	$(".back-login").click(function () {
	$("#loginbox").show();
	$("#forgotbox").hide();
	return false;
	});
});

// END ----------------------------- 2




// 4 - CLOSE OPEN SLIDERS BY CLICKING ELSEWHERE ON PAGE -------------------------------------------------------------------------

$(document).bind("click", function (e) {
    if (e.target.id != $(".showhide-account").attr("class")) $(".account-content").slideUp();
});

$(document).bind("click", function (e) {
    if (e.target.id != $(".action-slider").attr("class")) $("#actions-box-slider").slideUp();
});
// END ----------------------------- 4
 
 
