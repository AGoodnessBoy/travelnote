package ink.moming.travelnote.data;

/**
 * Created by admin on 2018/3/1.
 */

public class NoteBean {
    int noteid;
    String notetext;
    String noteimage;
    String noteuser;
    String notetime;

    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public String getNotetext() {
        return notetext;
    }

    public void setNotetext(String notetext) {
        this.notetext = notetext;
    }

    public String getNoteimage() {
        return noteimage;
    }

    public void setNoteimage(String noteimage) {
        this.noteimage = noteimage;
    }

    public String getNoteuser() {
        return noteuser;
    }

    public void setNoteuser(String noteuser) {
        this.noteuser = noteuser;
    }

    public String getNotetime() {
        return notetime;
    }

    public void setNotetime(String notetime) {
        this.notetime = notetime;
    }
}
