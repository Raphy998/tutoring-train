package at.bsd.tutoringtrain.data.mapper.views;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class JsonUserViews {
    public static class Out {
        public static class Register {}
        public static class Update {}
        public static class UpdateOwn {}
        public static class Block {}
        public static class UpdatePassowrd {}
    }
    
    public static class In {
        public static class Register {}
        public static class Get {}
    }
}
