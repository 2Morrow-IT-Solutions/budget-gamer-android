package com.tomorrowit.budgetgamer.utils

import org.junit.Assert
import org.junit.Test

class LogicTest {

    @Test
    fun isFieldCorrect_isCorrect() {
        // Testing Names
        Assert.assertEquals(true, Logic.isFieldCorrect(FieldType.Name, "Alex"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Name, ""))

        // Testing Emails
        Assert.assertEquals(true, Logic.isFieldCorrect(FieldType.Email, "alex@alex.com"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Email, "alex@alex"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Email, "alexalex.com"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Email, "alex"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Email, "alex@alex."))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Email, ""))

        // Testing Passwords
        Assert.assertEquals(true, Logic.isFieldCorrect(FieldType.Password, "Parola1!"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "parola1!"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "Parola12"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "Parola!!"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "parolata"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "!@#$%^&*"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "12345678"))
        Assert.assertEquals(false, Logic.isFieldCorrect(FieldType.Password, "PAROLATA"))
    }
}