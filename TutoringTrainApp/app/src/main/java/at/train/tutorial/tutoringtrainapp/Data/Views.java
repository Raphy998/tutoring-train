package at.train.tutorial.tutoringtrainapp.Data;

/**
 * Created by Moe on 06.01.2018.
 */

public interface Views {
    public static interface User{
     public static interface Out{
         public static interface Register{};
         public static interface RegisterNoPassword{};
         public static interface Login{};
         public static interface create{};
     };
    }
    public static interface Entry{
        public static interface In{
            public static interface loadNewest{};
        }
    }
    public static interface Comment{
        public static interface In{
            public static interface loadNewest{};
        }
        public static interface Out{
            public static interface create{};
        }
    }

    public static interface Error{}
}
