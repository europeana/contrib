package gr.ntua.ivml.awareness.db;

import gr.ntua.ivml.awareness.util.MongoDB;

import java.io.InputStream;

import com.mongodb.gridfs.GridFSInputFile;

/*Just a lame DAO emulator to keep the usage of GridFS consistent with the rest of the 
 * interactions with mongodb, mainly because morphia does not support GridFS*/

public class GridFSDAO {

	public String saveFile(InputStream is, String fileName){
		GridFSInputFile file = MongoDB.getGridFS().createFile(is, fileName);
		file.save();
		return file.getId().toString();
	}
}
