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
public class Views {
    public static class Offer {
        public static class In {
            public static class Create {};
            public static class Update extends Create {};
        };
        public static class Out {
            public static class Public {};
        };
    }
    
    public static class User {
        public static class In {
            public static class Register {};
            public static class Update extends Register {};
        };
        public static class Out {
            public static class Public {};
            public static class Private extends Public {};
        };
    }
    
    public static class Subject {
        public static class In {
            public static class Create {};
            public static class Update extends Create {};
        };
        public static class Out {
            public static class Public {};
        };
    }
    
    public static class Block {
        public static class In {
            public static class Create {};
        };
    }
}
