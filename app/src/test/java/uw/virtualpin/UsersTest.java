package uw.virtualpin;

import org.junit.Test;

import uw.virtualpin.Data.Users;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Created by Austin Ingraham on 12/7/2016.
 */
public class UsersTest {
    private String USERNAME = "August";
    private String PASSWORD = "testPass";
    private String FIRST_NAME = "William";
    private String LAST_NAME = "Smith";
    private String EMAIL = "will.s@gmail.com";

    private Users basicUsersFactoryFullArguments() {
        return new Users(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, EMAIL);
    }

    @Test
    public void testUserConstructorTwoArguments() {
        //notice that the arguments are reversed for this constructor!
        assertNotNull(new Users(PASSWORD, USERNAME));
    }

    @Test
    public void testUserConstructorFullArguments() {
        assertNotNull(new Users(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, EMAIL));
    }

    @Test
    public void testUserEmailGetter() {
        try {
            Users u = basicUsersFactoryFullArguments();
            assertEquals(u.getEmail(), EMAIL);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testUserUsernameGetter() {
        try {
            Users u = basicUsersFactoryFullArguments();
            assertEquals(u.getUsername(), USERNAME);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testUserFirstNameGetter() {
        try {
            Users u = basicUsersFactoryFullArguments();
            assertEquals(u.getFirstName(), FIRST_NAME);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testUserLastNameGetter() {
        try {
            Users u = basicUsersFactoryFullArguments();
            assertEquals(u.getLastName(), LAST_NAME);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testUserPasswordGetter() {
        try {
            Users u = basicUsersFactoryFullArguments();
            assertEquals(u.getPassword(), PASSWORD);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }
}
