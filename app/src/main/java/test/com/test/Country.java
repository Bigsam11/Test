package test.com.test;

import java.util.ArrayList;

public class Country {

    public String name;

    public String capital;

    public ArrayList<Currency> currencies = new ArrayList<>();

    public ArrayList<Language> languages = new ArrayList<>();

    public String flag;


    public class Currency {

        public String name;
    }


    public class Language {

        public String name;
    }
}
