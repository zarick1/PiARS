package krsto.zaric.shoppinglist;

public class CharacterModel {
    private String text;
    private boolean bul;
    private String taskId;

    public CharacterModel(String text, boolean bul, String taskId) {
        this.text = text;
        this.bul = bul;
        this.taskId = taskId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text)  {
        this.text = text;
    }

    public boolean isBul() {
        return bul;
    }

    public void setBul(boolean bul) {
        this.bul = bul;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
