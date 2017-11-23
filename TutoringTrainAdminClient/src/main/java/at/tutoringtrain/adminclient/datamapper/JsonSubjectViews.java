package at.tutoringtrain.adminclient.datamapper;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class JsonSubjectViews {
    public static class Out {
        public static class Register {}
        public static class Update {}

        public static class UpdateState {
            public UpdateState() {
            }
        }
    }
    
    public static class In {
        public static class Register {}
        public static class Get {}
    }
}