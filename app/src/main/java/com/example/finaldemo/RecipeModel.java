package com.example.finaldemo;

import java.util.HashMap;
import java.util.Map;

public class RecipeModel {
    public String name;
    public String recipe;

    public Map<String, String> toJson() {
        Map<String, String> ret = new HashMap<>();
        ret.put("name", name);
        ret.put("recipe", recipe);
        return ret;
    }

    public RecipeModel(Map<String, String> data) {
        this.name = data.get("name");
        this.recipe = data.get("recipe");
    }

    public RecipeModel(String name, String recipe) {
        this.name = name;
        this.recipe = recipe;
    }

    @Override
    public String toString() {
        return name + " shared:\n" + recipe;
    }
}
