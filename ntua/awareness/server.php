<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Request-Headers: application/json');
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");

if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
    header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");

header('Content-Type: application/json');

$request = trim(str_replace('europeana/server.php/', '', $_SERVER['REQUEST_URI']), '/');
$request = explode('/', $request);

if ($postdata = file_get_contents("php://input")) {
	foreach (explode("&", $postdata) as $postvars) {
		$keyval = explode("=", $postvars);
		$keyval[1] = urldecode($keyval[1]);
		$_REQUEST[$keyval[0]] = $keyval[1];
	}
	// error_log(var_export($_REQUEST, true));
}

isset($_REQUEST['type']) || $_REQUEST['type'] = '';

if ($_REQUEST['type'] == 'login') {
	$data = $_REQUEST;
	if (isset($_REQUEST['username']) && $_REQUEST['username'] == 'jokke') {
		$data = array('username' => '2354', 'fullName' => 'Joakim Nygård');
	} else {
		header("HTTP/1.0 400 Bad Request");
		$data = array('message' => 'Invalid request');
	}
	echo json_encode($data);
}

if ($_REQUEST['type'] == 'list') {
	$data = array('values' => array("3" , "1", "3" , "1", "3" , "1", ));// , "5058a1888aa144d4d986d9e9" , "5058a28e8aa103f3cfde5d52", "5058490f8aa1d838fc56a829" , "50584c078aa135e16443529c" , "5058a1888aa144d4d986d9e9" , "5058a28e8aa103f3cfde5d52", "5058490f8aa1d838fc56a829" , "50584c078aa135e16443529c" , "5058a1888aa144d4d986d9e9"));
	$data['totalSize'] = count($data['values']);
	echo json_encode($data);
}

if ($_REQUEST['type'] == 'story') {
	$names = array('Joakim Nygard', 'Peter Thorn', 'Jakob Thorbek');
	$images = array(
		'http://www.copper.org/education/history/images/copboy4.gif',
		'http://blogs.varsity.co.uk/wp-content/uploads/2011/10/history-flag.jpg',
		'http://www.stbons.org/Mainfolder/history-virginia.jpg',
		'http://www.hedinghamcastle.co.uk/castle/hedingham/castle_history_drawing.jpg',
	);
	$data = array(
		'creator' => '50810401bf9594d11fa039b6', 
		'coverImage' => 'none',//$images[array_rand($images)],
		// 'likes' => rand(0, 10),
		'_id' => array('$oid' => $_REQUEST['id']),
		'description' => "On July ’69, the Apollo 11 is landing on the moon. USA has achieved to send 11 astronauts and the two first men who walked on the moon....
		Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
		Expanding transfer stream, overheating signal. There appears to be an enhanced signal next to the protocol which affects virtual causes at the digital firewall. You have to trace the subnet, because the number string is near the causes. The overheating matrix around the partition. Shifting the critical firewall affects disturbance at the compressed domain. The transfer stream socket seems to be not disconnected. Overloading broadcasted localities modulates the weak backups. I need to invert the server, because the number string seems to be next to the damage. The broadcasted protocol seems to be extending - I must invert the mainframe before mainframe damages the compressed affects to reverse. Temporal socket needs to be converted, because the compressed subnet seems to be reset. There must have been temporal disturbance within the filtered removed network.I should invert the number strings. There has to be special disturbance inside the virtual removed access point. The compressed overlays enhance the temporal number strings in the frequencies. Shifting the virtual socket damages damage in the filtered domain.I should invert the overlay, because the signal seems to be next to the causes.",
		'dateCreated' => array('$date' => date('Y-m-d\Th:m:s.Z\Z'))
	);
	switch ($_REQUEST['id']) {
		case '1':
			$data['title'] = 'Some story';
			$data['description'] = 'Short introduction';
			$data["storyObjects"] = array(array('position' => 0, 'StoryObjectID' => '1'), array('position' => 3, 'StoryObjectID' => "2"), array('position' => 5, 'StoryObjectID' => "4"));
			break;
		case '2':
			$data['title'] = 'Another breaker';
			$data["storyObjects"] = array(array('position' => 4, 'StoryObjectID' => '1'), array('position' => 5, 'StoryObjectID' => "3"), array('position' => 7, 'StoryObjectID' => "5"));
			break;
		case '3':
			$data['title'] = 'One more';
			// $data["storyObjects"] = array(8 => "1" , 10 => "3", 1 => "5");
			$data["storyObjects"] = array(array('position' => 8, 'StoryObjectID' => '1'), array('position' => 10, 'StoryObjectID' => "3"), array('position' => 1, 'StoryObjectID' => "5"));
			break;
		default:
			# code...
			break;
	}
	echo json_encode($data);
}

if ($_REQUEST['type'] == 'objects') {
	$data = array(
		'values' => array(1, 2, 3, 4, 5)
	);
	echo json_encode($data);
}

if ($_REQUEST['type'] == 'object') {
	$data = array(
		'_id' => array('$oid' => $_REQUEST['id']),
		'dateCreated' => array('$date' => date('Y-m-d\Th:m:s.Z\Z'))
	);
	if (strlen($_REQUEST['id']) > 2) {
		$_REQUEST['id'] = rand(1, 5);
	}
	switch ($_REQUEST['id']) {
		case 1:
			$data['type'] = 'video';
			$data['title'] = "First Moon Landing";
			$data['description'] = 'The first moon landing was important';
			$data['url'] = 'http://www.youtube.com/embed/RMINSD7MmT4';
			break;
		case 2:
			$data['type'] = 'image';
			$data['title'] = "An image";
			$data['description'] = 'Lorem ingres_field_precision(result, index)';
			$data['url'] = 'http://www.stbons.org/Mainfolder/history-virginia.jpg';
			break;
		case 3:
			$data['type'] = 'video';
			$data['title'] = "Some video";
			$data['description'] = 'for ($i=0; $i < ; $i++) { 
				# code...
			}';
			$data['url'] = 'http://www.youtube.com/embed/0kRAKXFrYQ4';
			break;
		case 4:
			$data['type'] = 'image';
			$data['title'] = "Castle";
			$data['description'] = '';
			$data['url'] = 'http://www.hedinghamcastle.co.uk/castle/hedingham/castle_history_drawing.jpg';
			break;
		case 5:
			$data['type'] = 'image';
			$data['title'] = "Flag";
			$data['description'] = 'wddx_deserialize() w32api_deftype(typename, member1_type, member1_name)';
			$data['url'] = 'http://blogs.varsity.co.uk/wp-content/uploads/2011/10/history-flag.jpg';
			break;
	}
	echo json_encode($data);
}

return;

if ($_SERVER['REQUEST_METHOD'] == "GET" || empty($_POST['title'])) {
	echo json_encode(array('title' => 'monkey'));
} else {
	error_log($_POST['title']);
	echo json_encode(array('title' => strrev($_POST['title'])));
}