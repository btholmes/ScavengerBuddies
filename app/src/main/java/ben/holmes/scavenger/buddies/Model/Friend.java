package ben.holmes.scavenger.buddies.Model;

import java.io.Serializable;


public class Friend implements Serializable {
	private long id;
	private String uid;
	private String name;
	private int photo;
	private String photoUrl;

	public Friend(){

	}

	public Friend(long id, String name, int photo) {

		this.id = id;
		this.name = name;
		this.photo = photo;
	}

	public Friend(String name, int photo) {
		this.name = name;
		this.photo = photo;
	}

	public Friend(User user){
		this.uid =  user.getUid();
		if(user.getPhotoUrl() != null){
			this.photoUrl = user.getPhotoUrl();
		}
		this.photo = 1;
		this.name = user.getDisplayName();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public int getPhoto() {
		return photo;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
