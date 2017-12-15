/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.utils;

/**
 *
 * @author Elias
 */
public interface Views {
    public static interface Entry {
        public static interface In {
            public static interface Create {};
            public static interface Update extends Create {};
        };
        public static interface Out {
            public static interface Public {};
        };
    }
    
    public static interface User {
        public static interface In {
            public static interface Register {};
            public static interface Update extends Register {};
            public static interface Promote {};
        };
        public static interface Out {
            public static interface Public {};
            public static interface Private extends Public {};
        };
    }
    
    public static interface Subject {
        public static interface In {
            public static interface Create {};
            public static interface Update extends Create {};
        };
        public static interface Out {
            public static interface Public {};
        };
    }
    
    public static interface Block {
        public static interface In {
            public static interface Create {};
        };
    }
    
    public static interface Error {
        public static interface Out {
            public static interface Public {};
        };
    }
}
