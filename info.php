 <?php
 //Written by Austin Ingraham
 //7th December, 2016
ini_set('display_errors', '1');
error_reporting(E_ALL);
$DNE = "DoesNotExist";

$command = getCommand('cmd');

//CONNECT TO THE DATABASE
$dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=_450team8';
$username = '_450team8';
$password = '/////SCRUBBED/////';

//CARRY OUT COMMAND
try {
	$db = new PDO($dsn, $username, $password);
	$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

	if ($command['set'] == true) {
		$command = $command['value'];
		
		if ($command == "login") {
			loginUsing($db, "username");
		}
		else if ($command == "login_email") {
			loginUsing($db, "email");
		}
		else if ($command == "select*users") {
			$sqlStatement = 'SELECT email, username, password FROM User';
			$user_query = $db->query($sqlStatement);
			$users = $user_query->fetchAll(PDO::FETCH_ASSOC);
			if ($users) {	
				echo json_encode($users);
			}
		}
		else if ($command == "get_userdata") {
			$param_username = getCommand('username');
			$username;
			if( $param_username['set'] == true) {
				$username = $param_username['value'];
				$sqlStatement = "SELECT * FROM User WHERE username='$username'";
				$user_query = $db->query($sqlStatement);
				$user = $user_query->fetch(PDO::FETCH_ASSOC);
				if ($user) {	
					echo json_encode($user);
				} else {
					declareError("No user found.");
				}
			} else {
				declareError("Missing parameter: username.");
			}
		}
		else if ($command == "register_user") {

			//get inputs for course information
			$param_email = getCommand('email');
			$param_username = getCommand('username');
			$param_password = getCommand('password');
			$param_firstName = getCommand('firstName');
			$param_lastName = getCommand('lastName');
			if($param_email['set'] == true && $param_username['set'] == true &&
				$param_password['set'] == true && $param_firstName['set'] == true 
				&& $param_lastName['set'] == true) { 
				$email = $param_email['value'];
				$username = $param_username['value'];
				$password = $param_password['value'];
				$firstName = $param_firstName['value'];
				$lastName = $param_lastName['value'];
				
				//CHECK FOR VALID VALUES
				if(checkEmailValid($email) && 
				  !credentialExists($db, $email, 'email')) { //email not returned sanitized, not secure
					if(checkUsernameValid($username) && 
					  !credentialExists($db, $username, 'username')) {
						if(checkPasswordValid($password)) {
							if(checkNameValid($firstName, "First name") &&
							   checkNameValid($lastName, "Last name")) {
								//build query
								$sql = "INSERT INTO User(email, username, password, Fname, Lname, userImage) VALUES ('$email', '$username', '$password', '$firstName', '$lastName', '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')";
							 
								//attempts to add record
								if ($db->query($sql)) {
									echo '{"registration": "success"}';
								} else {
									echo '{"registration": "failure")';
								}
							}
						}
					}
				}
			} else {
				declareError("Some required credentials missing.");
			}   
		}
		else if ($command == "update_avatar") {
			$param_id = getCommand('id');
			if($param_id['set'] == true) {
				$id = $param_id['value'];
				if(isset($_POST['image'])) {
					$image = chunk_split($_POST['image']);
					$sql = "UPDATE `_450team8`.`User` SET `userImage`='$image' WHERE `idUser`='$id';";
					if ($db->query($sql)) {
						echo json_encode(array('success' => true));
					} else {
						echo json_encode(array('success' => false));
					}
				} else {
					declareError('Missing image _POST.');
				}
			} else {
				declareError('Missing id parameter.');
			}
		}
		else if ($command == "select*pins") {
			$sqlStatement = 'SELECT pinID, creator, latitude, longitude, message FROM Pin';
			$user_query = $db->query($sqlStatement);
			$users = $user_query->fetchAll(PDO::FETCH_ASSOC);
			if ($users) {	
				echo json_encode($users);
			}
		}
		else if ($command == "nearby_pins") {
			
			$param_latitude = getCommand('latitude');
			$param_longitude = getCommand('longitude');
			if($param_latitude['set'] == true && $param_longitude['set'] == true) {
				$latitude = $param_latitude['value'];
				$longitude = $param_longitude['value'];
				
				$sqlStatement = "SELECT pinID, creator, latitude, longitude, message FROM Pin WHERE ((abs($latitude) - abs(latitude) BETWEEN -0.01 AND 0.01) AND (abs($longitude) - abs(longitude) BETWEEN -0.02 AND 0.02));";
				$db_query = $db->query($sqlStatement);
				$nearby_pins = $db_query->fetchAll(PDO::FETCH_ASSOC);
				if ($nearby_pins) {	
					echo json_encode($nearby_pins);
				}
			} else {
				declareError("Missing latitude or longitude information.");
			}
		}
		else if ($command == "pin_history") {		
			$param_username = getCommand('username');
			if($param_username['set'] == true) {
				$username = $param_username['value'];
				$sqlStatement = "SELECT pinID, creator, latitude, longitude, message FROM Pin WHERE creator='$username'";
				$pin_query = $db->query($sqlStatement);
				$pins = $pin_query->fetchAll(PDO::FETCH_ASSOC);
				if ($pins) {	
					echo json_encode($pins);
				}
			}
		}
		else if ($command == "pin_history_count") {
			$param_username = getCommand('username');
			if($param_username['set'] == true) {
				$username = $param_username['value'];
				$sqlStatement = "SELECT pinID FROM Pin WHERE creator='$username'";
				$pin_query = $db->query($sqlStatement);
				$count = $pin_query->rowCount();
				if ($count) {	
					echo json_encode(array('count' => "$count"));
				}
			}
		}
		else if ($command == "new_pin") {
			//get inputs for the pin
			$param_creator = getCommand('username');
			$param_latitude = getCommand('latitude');
			$param_longitude = getCommand('longitude');
			$param_message = getCommand('message');
			if($param_creator['set'] == true && $param_latitude['set'] == true &&
											  $param_longitude['set'] == true) {
				$creator = $param_creator['value'];
				$latitude = $param_latitude['value'];
				$longitude = $param_longitude['value'];
				
				$image_yes = false;
				$image;
				if(isset($_POST['image'])) {
					$image_yes = true;
					$image = chunk_split($_POST['image']);
				}
				
				//CHECK FOR VALID VALUES
				//BUILD QUERY
				$sql = "INSERT INTO Pin(creator, latitude, longitude";
				if($param_message['set'] == true) {
					$sql .= ", message"; 
				}
				if($image_yes) {
					$sql.= ", image";
				}
				$sql .= ") VALUES ('$creator', '$latitude', '$longitude'";
				
				if($param_message['set'] == true) {
					$message = $param_message['value'];
					$sql .= ", '$message'"; 
				}
				if($image_yes) {
					$sql.= ", '$image'";
				}
				$sql .= ");";
				
				if($image_yes == true || $param_message['set'] == true) {
					//attempts to add record
					if ($db->query($sql)) {
						echo json_encode(array('success' => true));
					} else {
						echo json_encode(array('success' => false));
					}
				} else {
					declareError("Need either a message or image to make a post.");
				}
			} else {
				declareError("username, latitude, or longitude parameter missing.");
			}   
		}
		else if ($command == "update_pin") {

			$param_id = getCommand('id');
			if($param_id['set'] == true) {
				$id = $param_id['value'];
				
				$param_message = getCommand('message');
				$image_yes = false;
				$message;
				$image;
				
				//CHECK FOR VALID VALUES, BUILD QUERY
				$sql = "UPDATE `_450team8`.`Pin` SET ";
				if($param_message['set'] == true) {
					$message = $param_message['value'];
					$sql .= "`message`='$message' "; 
				}
				if(isset($_POST['image'])) {
					$image = chunk_split($_POST['image']);
					$image_yes = true;
					if($param_message['set'] == true) {
						$sql .= ", ";
					}
					$sql.= "`image`='$image' ";
				}
				$sql .= "WHERE `pinID`='$id';";
				
				if($image_yes == true || $param_message['set'] == true) {
					if ($db->query($sql)) { //attempts to add record
						echo json_encode(array('success' => true));
					} else {
						echo json_encode(array('success' => false));
					}
				} else {
					declareError("Need either a message or image to make a post.");
				}
			} else {
				declareError("Missing parameter: id");
			}
		}
		else if ($command == "get_pin") {
			$param_id = getCommand('id');
			if( $param_id['set'] == true) {
				$id = $param_id['value'];
				$sqlStatement = "SELECT * FROM Pin WHERE pinID='$id'";
				$pin_query = $db->query($sqlStatement);
				$pin = $pin_query->fetch(PDO::FETCH_ASSOC);
				if ($pin) {	
					echo json_encode($pin);
				} else {
					declareError("No pin found.");
				}
			} else {
				declareError("Missing parameter: id");
			}			
		}
		else if ($command == "test_display_pin") {
			$param_id = getCommand('id');
			$id = 0;
			if( $param_id['set'] == true) {
				$id = $param_id['value'];
			}
			$sqlStatement = "SELECT pinID, creator, latitude, longitude, message, image FROM Pin WHERE pinID='$id'";
			$pin_query = $db->query($sqlStatement);
			$pin = $pin_query->fetch(PDO::FETCH_ASSOC);
			if ($pin) {	
				echo "Creator: ".$pin['creator'];
				echo "<br/>latitude: ".$pin['latitude'].",   longitude: ".$pin['longitude'];
				echo "<br/>Message: ".$pin['message'];
				$raw = $pin['image'];
				if($raw) {
					echo "<br/>Image:<br/><img src=\"data:image/png;base64,$raw\"/>";
				} else {
					echo "<br/>Image:<br/><i>null</i>";
				}
			} else {
				echo "No pin found, did you forget to pass an id?";
			}
		}
		else {
			declareError("Command '$command' not recognized.");
		}
	}
	$db = null;
} catch (PDOException $e) {
	$error_message = $e->getMessage();
	declareError('PDO Exception.');
	//echo "<br/>$error_message";
	exit();
}
function loginUsing($theDB, $credential_type) {
	$param_cred = getCommand($credential_type);
	$param_pass = getCommand('password');
	if($param_cred['set'] == true && $param_pass['set'] == true) {
		$user = $param_cred['value'];
		$password = $param_pass['value'];
		if(login($theDB, $user, $password, $credential_type)) {			
			echo json_encode(array('loggedin' => true));
		} else {
			echo json_encode(array('loggedin' => false));
		}
	} else {
		declareError("username or password parameter missing.");
	}
}
function login($theDB, $theCredential, $thePassword, $credential_type) {
	$sqlStatement = "SELECT `$credential_type` FROM User WHERE $credential_type = '$theCredential' AND `password` = '$thePassword'";
	$query = $theDB->query($sqlStatement);
	$count = $query->rowCount();

    //if the $count = 1 or more return true else return false
    if($count >= 1) {
        return true;
    } else {
        return false;
    }
}
//REMEMBER: this function throws an error IF IT EXISTS!
function credentialExists($theDB, $theCredential, $credential_type) {
	$sqlStatement = "SELECT `$credential_type` FROM User WHERE $credential_type = '$theCredential'";
	$query = $theDB->query($sqlStatement);
	$count = $query->rowCount();

    //if the $count = 1 or more return true else return false
    if($count >= 1) {
		declareError("That $credential_type is already in use.");
        return true;
    } else {
        return false;
    }
}
function checkEmailValid($theEmail) {
	// Remove all illegal characters from email
	$theEmail = filter_var($theEmail, FILTER_SANITIZE_EMAIL);

	// Validate e-mail
	if (!filter_var($theEmail, FILTER_VALIDATE_EMAIL) === false) {
		if(strlen($theEmail) <= 254) {
			return true;
		} else {
			declareError("Size of email too large, exceeds 254 characters.");
		}
	} else {
		declareError("$theEmail is not a valid email address.");
	}
	return false;
}
function checkUsernameValid($theUsername) {
	if (preg_match('/^[a-zA-Z0-9_-]{5,20}$/', $theUsername)) {
		return true;
	} else {
		declareError("Username not valid.");
		return false;
	}
}
function checkPasswordValid($thePassword) {
	if(strlen($thePassword) > 5 && strlen($thePassword) < 31) {
		return true;
	} else {
		declareError("Password length must be between 6 and 30 characters long.");
	}
	return false;	
}
function checkNameValid($theName, $nameType) {
	if (strlen($theName) >= 1 && strlen($theName) <= 20 ) {
		return true;
	} else {
		declareError("$nameType length must be within 1 and 20 characters.");
		return false;
	}
}
//If the given command exists, return its value, otherwise return DoesNotExist
function getCommand($theCommand) {
	if(isset($_GET[$theCommand])) {
		return array("set" => true, "value" => $_GET[$theCommand]);
	} else {
		return array("set" => false, "value" => "DoesNotExist");
	}
}
function declareError($theMessage) {
	echo json_encode(array("ERROR" => $theMessage));
}
?>
