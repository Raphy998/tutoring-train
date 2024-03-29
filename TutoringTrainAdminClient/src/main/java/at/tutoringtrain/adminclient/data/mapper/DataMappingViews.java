package at.tutoringtrain.adminclient.data.mapper;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface DataMappingViews {
    public static interface Entry {
        public static interface In {
            public static interface Get{};
            public static interface Create{};
        }   
        public static interface  Out {
            public static interface Create{};
        }
    }
    
    public static interface User {
        public static interface Out {
            public static interface Register {}
            public static interface Update {}
            public static interface UpdateOwn {}
            public static interface Block {}
            public static interface UpdatePassowrd {}
            public static interface UpdateRole {}
        } 
        public static interface In {
            public static interface Register {}
            public static interface Get {}
        }
    }
    
    public static interface Subject {
        public static interface Out {
            public static interface Register {}
            public static interface Update {}
            public static interface UpdateState {}
        }
        public static interface In {
            public static interface Register {}
            public static interface Get {}
        }
    }
}
