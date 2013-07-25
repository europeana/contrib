$(function() {
	$("#dialog").dialog({
		bgiframe: true,
		autoOpen: false,
		height: 520,
		width: 520,
		resizable: false,
		modal: true,
		draggable: false
	});
	
	$("#datepicker").datepicker({
		altField: '#actualDate',
		altFormat: 'dd-mm-yy',
		maxDate: '+1y',
		minDate: 0,
		numberOfMonths: 2
	});

});


function deleteCalendarScheduledTask(confirmationMessage) {
	deleteScheduledTask(confirmationMessage, $('#taskId').val());
}

function deleteScheduledTask(confirmationMessage, taskId) {
	var Location = pageContext + '/scheduler/Scheduler.action?deleteScheduledTask&taskId=' + taskId;
	var answer = confirm (confirmationMessage);
	if (answer) {
		location.href = Location;
	}
	else {
		
	}
}

function addslashes(str) {		
	return (str+'').replace(/([\\"'])/g, "\\$1").replace(/\0/g, "\\0");		
}

/*********************************************/
/* New Task                                  */
/*********************************************/
function ingestDialog(formAction, dataProviderName, dataSourceId, fullIngest) {
	$('#export').hide();
	
	if(fullIngest == 'true') {
		$('#incrementalIngestRadio').attr('checked', '');
		$('#fullIngestRadio').attr('checked', 'checked');
		$('#fullIngest').show();
	}
	else if(fullIngest == 'false'){
		$('#incrementalIngestRadio').attr('checked', 'checked');
		$('#fullIngestRadio').attr('checked', '');
		$('#fullIngest').show();
	}
	else { //fullIngest is null or empty
		$('#fullIngest').hide();
	}
	
	popupDialog(formAction, 'scheduleHarvest', dataProviderName, dataSourceId);
}

function exportDirDialog(formAction, dataProviderName, dataSourceId) {
	$('#export').show();
	$('#fullIngest').hide();
	popupDialog(formAction, 'scheduleExportFilesystem', dataProviderName, dataSourceId);
}

function popupDialog(formAction, submitName, dataProviderName, dataSourceId) {
	setCommonProperties(formAction, submitName, dataProviderName, dataSourceId);

	$('#deleteTaskButton').hide();
	
	selectFrequency();
	
	$('#dialog').dialog('open');
}

/*********************************************/
/* Load Task                                 */
/*********************************************/
function ingestDialogLoad(formAction, dataProviderName, dataSourceId, fullIngest, taskId, frequency, xmonths, fullDate) {
	$('#export').hide();
	if(fullIngest != null && fullIngest != '') {
		if(fullIngest == 'true') {
			$('#incrementalIngestRadio').attr('checked', '');
			$('#fullIngestRadio').attr('checked', 'checked');
		}
		else {
			$('#incrementalIngestRadio').attr('checked', 'checked');
			$('#fullIngestRadio').attr('checked', '');
		}
		$('#fullIngest').show();
	}
	
	popupDialogLoad(formAction, 'scheduleHarvest', dataProviderName, dataSourceId, taskId, frequency, xmonths, fullDate);
}

function exportDirDialogLoad(formAction, dataProviderName, dataSourceId, exportDir, recordsPerFile, taskId, frequency, xmonths, fullDate) {
	$('#export').show();
	$('#exportDirectoryValue').val(exportDir);
	$('#recordsPerFileValue').val(recordsPerFile);
	$('#fullIngest').hide();
	popupDialogLoad(formAction, 'scheduleExportFilesystem', dataProviderName, dataSourceId, taskId, frequency, xmonths, fullDate);
}

function popupDialogLoad(formAction, submitName, dataProviderName, dataSourceId, taskId, frequency, xmonths, fullDate) {
	setCommonProperties(formAction, submitName, dataProviderName, dataSourceId);

	$('#deleteTaskButton').show();
	$('#editing').val('true');
	$('#taskId').val(taskId);
	
	selectFrequency(frequency);
	if(frequency == 'XMONTHLY') {
		$('#xmonths').val(xmonths);
	}
	else {
		$('#xmonths').val(1);
	}

	var fullDateParts = fullDate.split(" "); // "HH:mm dd/MM/yyyy"
	var dateParts = fullDateParts[1].split("/");
	$('#datepicker').datepicker("setDate", new Date(dateParts[2], dateParts[1] - 1, dateParts[0]));

	var timeParts = fullDateParts[0].split(":");
	$('#hourProperty').val(timeParts[0]);
	$('#minuteProperty').val(timeParts[1]);
	
	$('#dialog').dialog('open');
}


function setCommonProperties(formAction, submitName, dataProviderName, dataSourceId) {
	$('#dialog').data('title.dialog', '<fmt:message key="common.schedule" /> ' + dataProviderName + ' - ' + dataSourceId); 
	
	$('#dialog > form').attr('action', pageContext + formAction);
	$('#popupSubmitName').attr('name', submitName);
	$('#dialog > form > input#dataSourceId').attr('value', dataSourceId);
}

function selectFrequency(frequency) {
	var frequencies = new Array('ONCE', 'DAILY', 'WEEKLY', 'XMONTHLY');
	for(index in frequencies) {
		$('#' + frequencies[index]).attr('checked', '');
	}
	
	if(frequency != null && frequency != '') {
		$('#' + frequency).attr('checked', 'checked');
	}
	else {
		$('#ONCE').attr('checked', 'checked');
	}
}
