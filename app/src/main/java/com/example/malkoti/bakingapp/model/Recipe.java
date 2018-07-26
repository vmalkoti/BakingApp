package com.example.malkoti.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 */
public class Recipe {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;

    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

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

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Ingredients of the recipe
     */
    public static class Ingredient implements Parcelable {
        private float quantity;
        private String measure;
        private String ingredient;

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            @Override
            public Object createFromParcel(Parcel source) {
                return new Ingredient(source);
            }

            @Override
            public Object[] newArray(int size) {
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
    public static class Step {
        private int id;
        private String shortDescription;
        private String description;
        private String videoURL;
        private String thumbnailURL;

        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
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
    }
}
