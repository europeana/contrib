<?php	
	function getTagContents($xml, $tagName) {
		$tag = $xml->getElementsByTagName($tagName);
		
		if ($tag->length == 1)
			return $xml->getElementsByTagName($tagName)->item(0)->nodeValue;
		if ($tag->length > 1) {			
			for($i=0;$i<$tag->length;$i++)
				$values[] = $xml->getElementsByTagName($tagName)->item($i)->nodeValue;
			return $values;
		} else
			return "";		
	}
	
	function getArrayContents($array) {		
		$result = "";
		$headers = array_keys($array);
		if (count($headers) != 0)
			for($i=0; $i<count($headers); $i++) {
				$currResult = $array[$headers[$i]];
				if (is_array($currResult))
					$result .= getArrayContents($currResult);
				else
					$result .= $array[$headers[$i]]."\n";
			}

		return $result;
		//return "";
	}
	
	function xml2array($xml) {
		$result = array();		
		
		$element = $xml->getElementsByTagName("service");
		foreach ($element as $node) {
			$result[] = processNode($node);			
		}
		return $result;
	}

	
	function processNode($node) { 		
		$result = "";
		
		// Return the value direct if it is a XML Text Node
		if ($node->nodeType == XML_TEXT_NODE) { 
			$result = html_entity_decode(htmlentities($node->nodeValue, ENT_COMPAT, 'UTF-8'), 
                                     ENT_COMPAT,'ISO-8859-15');						
		} 
		// Retrieve the value
		else {					
			// Check if the field has child nodes
			if ($node->hasChildNodes()){
				$children = $node->childNodes;
				$childArray = "";
 
				// Loop through children
				for ($i=0; $i<$children->length; $i++) {				
					$child = $children->item($i);
 
					// #text field found, retrieve value
					if (stristr($child->nodeName, "#text")) {
						$text = processNode($child);
 						
						if (trim($text) != '') 
							$childArray = $text;							
					}	
					
					// No #text field found, process children
					else {						
						$childContents = processNode($child);		
						
						// Check if a node name already exists (arrays can't handle multiple instances with the same name)												
						if (isset($childArray[$child->nodeName]) || (nodeNameCounter($child, $child->nodeName) > 1)) 						
							$childArray[$child->nodeName][] = $childContents;																	
						// Else, just add it to the result array
						else 											
							$childArray[$child->nodeName] = $childContents;
					}
				}
				// Add the array to the $result				
				$result = $childArray;
			} 			
		}
		
		// Check for attributes
		if ($node->hasAttributes()) { 
			$attributes = $node->attributes;

			if(!is_null($attributes)) {
				$attrArray = array();
				$nodeName = $node->nodeName;
								
				foreach ($attributes as $key => $attr) 					
					$attrArray["@".$attr->name] = $attr->value;						
				
				$tmpResult = $result;					
				if (is_array($result) && ($result != "")) 			
					$result = array_merge($attrArray, $tmpResult);
				else {
					$result = array();
					$attrArray["#text"] = $tmpResult;				
					$result = array_merge($result, $attrArray);
				}
			}
		} 		
		return $result;
	}
	
	function nodeNameCounter($node, $name) {				
		$parent = $node->parentNode;
		$i=0;
		
		foreach ($parent->childNodes as $child) 
			if (strstr($child->nodeName, $name))
				$i++;
		
		return $i;
	}
?>