package gr.ntua.ivml.awareness.persistent;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class Image {
	@Id
    public ObjectId id;
}
