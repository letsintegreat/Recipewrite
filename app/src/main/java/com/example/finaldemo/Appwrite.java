package com.example.finaldemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;

import io.appwrite.Client;
import io.appwrite.ID;
import io.appwrite.Query;
import io.appwrite.coroutines.CoroutineCallback;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.DocumentList;
import io.appwrite.models.User;
import io.appwrite.models.Session;
import io.appwrite.services.Account;
import io.appwrite.services.Databases;

public class Appwrite {
    private static Client client;
    private static Account account;
    private static Databases databases;

    private static final String projectID = "recipewrite";
    private static final String endpoint = "https://cloud.appwrite.io/v1";
    private static final String databaseID = "default";
    private static final String collectionID = "recipes";

    public static void init(Context context) {
        client = new Client(context);
        client.setEndpoint(endpoint);
        client.setProject(projectID);
        client.setSelfSigned(true);

        account = new Account(client);
        databases = new Databases(client);
    }

    public static void onGetAccount(CoroutineCallback<User> coroutineCallback) {
        try {
            /* Account.get is used to get the currently logged in user. */
            account.get(coroutineCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onLogin(String email, String password, CoroutineCallback<Session> coroutineCallback) {
        /* Allow the user to login into their account by providing a valid email and password combination. */
        account.createEmailSession(
                email,
                password,
                coroutineCallback
        );
    }

    public static void onCreateAccount(String email, String password, String name, CoroutineCallback<User> coroutineCallback) {
        try {
            /* Allow a new user to register a new account */
            account.create(
                    ID.Companion.unique(),
                    email,
                    password,
                    name,
                    coroutineCallback
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    public static void onLogout(CoroutineCallback<Object> coroutineCallback) {
        /* Logout the user. Use 'current' as the session ID to logout on this device, use a session ID to logout on another device. */
        account.deleteSession(
                "current",
                coroutineCallback
        );
    }

    public static void onAddRecipe(RecipeModel recipeModel, CoroutineCallback coroutineCallback) {
        try {
            /* Create a new Document. */
            databases.createDocument(
                    databaseID,
                    collectionID,
                    ID.Companion.unique(),
                    recipeModel.toJson(),
                    coroutineCallback
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    public static void listRecipes(String search, String lastDocumentId, CoroutineCallback<DocumentList> coroutineCallback) {
        ArrayList<String> queries = new ArrayList<>();

        if (!search.isEmpty()) {
            /* Perform the search on 'recipe' attribute */
            queries.add(Query.Companion.search("recipe", search));
        }

        if(!lastDocumentId.isEmpty()) {
            /* This retrieves the next page */
            queries.add(Query.Companion.cursorAfter(lastDocumentId));
        }

        try {
            /* Get a list of all the user's documents in a given collection. */
            databases.listDocuments(
                databaseID,
                collectionID,
                queries,
                coroutineCallback
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }
}
