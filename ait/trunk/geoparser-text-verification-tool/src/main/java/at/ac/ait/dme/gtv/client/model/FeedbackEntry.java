package at.ac.ait.dme.gtv.client.model;

/**
 * A feedback entry for a specific location / text fragment
 *
 * @author Manuel Bernhardt
 */
public class FeedbackEntry implements Comparable<FeedbackEntry> {

    @Override
    public int compareTo(FeedbackEntry o) {
        return o.getText().compareTo(this.getText());
    }

    public enum FeedbackType {
        NOT_A_PLACE("Not a place"), UNRECOGNIZED_PLACE("Unrecognized place");

        private String label;

        FeedbackType(String label) {
            this.label = label;
        }

        public String label() {
            return this.label;
        }
    }

    private FeedbackType type;

    private String text;

    private int offset;

    public FeedbackType getType() {
        return type;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public FeedbackEntry(FeedbackType type, String text, int offset) {
        this.type = type;
        this.text = text;
        this.offset = offset;
    }
}
