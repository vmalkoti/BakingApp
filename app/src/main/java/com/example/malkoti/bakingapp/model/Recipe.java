package com.example.malkoti.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe class for recipes objects
 * received in Baking App JSON response
 */
public class Recipe implements Parcelable {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[0];
        }
    };

    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    /*
     * Constructor for Parcelable
     */
    public Recipe(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.ingredients = new ArrayList<>();
        in.readTypedList(this.ingredients, Ingredient.CREATOR);
        this.steps = new ArrayList<>();
        in.readTypedList(this.steps, Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.id);
        out.writeString(this.name);
        out.writeTypedList(this.ingredients);
        out.writeTypedList(this.steps);
        out.writeInt(this.servings);
        out.writeString(this.image);
    }

    /* Getters */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }


    /**
     * Inner static class
     * Ingredients of the recipe
     */
    public static class Ingredient implements Parcelable {
        private float quantity;
        private String measure;
        private String ingredient;

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Ingredient>() {
            @Override
            public Ingredient createFromParcel(Parcel source) {
                return new Ingredient(source);
            }

            @Override
            public Ingredient[] newArray(int size) {
                return new Ingredient[0];
            }
        };

        public Ingredient(float quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
        }

        /* Constructor for Parcelable */
        public Ingredient(Parcel in) {
            this.quantity = in.readFloat();
            this.measure = in.readString();
            this.ingredient = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeFloat(this.quantity);
            out.writeString(this.measure);
            out.writeString(this.ingredient);
        }

        /* Getters */
        public float getQuantity() {
            return quantity;
        }

        public String getMeasure() {
            return measure;
        }

        public String getIngredient() {
            return ingredient;
        }

        /* Setters */
        public void setQuantity(float quantity) {
            this.quantity = quantity;
        }

        public void setMeasure(String measure) {
            this.measure = measure;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        @Override
        public String toString() {
            return getIngredient() + " - " + getQuantity() + " " + getMeasure();
        }
    }

    /**
     * Steps of the recipe
     */
    public static class Step implements Parcelable {
        private int id;
        private String shortDescription;
        private String description;
        private String videoURL;
        private String thumbnailURL;

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };

        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
        }

        /*
         * Constructor for Parcelable
         */
        protected Step(Parcel in) {
            this.id = in.readInt();
            this.shortDescription = in.readString();
            this.description = in.readString();
            this.videoURL = in.readString();
            this.thumbnailURL = in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(shortDescription);
            dest.writeString(description);
            dest.writeString(videoURL);
            dest.writeString(thumbnailURL);
        }

        public int getId() {
            return id;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Step) {
                Step passedStep = (Step) obj;
                return (passedStep.getId()==this.getId()
                        && passedStep.getDescription().equals(this.getDescription()));
            }

            return false;
        }
    }
}
