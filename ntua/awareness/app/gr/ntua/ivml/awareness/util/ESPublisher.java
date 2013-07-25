package gr.ntua.ivml.awareness.util;

import org.bson.types.ObjectId;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class ESPublisher {

	private Client client;
	
	public ESPublisher(String hostName, int port){
		client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress(hostName, port));
	}
	
	public void publish(BasicDBObject obj, String objectType){
		String str = JSON.serialize(obj);
		client.prepareIndex("awareness", objectType, ((ObjectId)obj.get("_id")).toString()).setSource(str)
		.execute()
		.actionGet();
	}
	
	public void unpublish(BasicDBObject obj, String objectType){
		client.prepareDelete("awareness", objectType, obj.getString("id")).execute().actionGet();
	}
	
	public void unpublish(String id, String objectType){
		client.prepareDelete("awareness", objectType, id).execute().actionGet();
	}
}
