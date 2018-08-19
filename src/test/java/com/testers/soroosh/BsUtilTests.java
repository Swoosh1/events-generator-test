package com.testers.soroosh;

import static org.junit.Assert.*;

import org.junit.Test;

import com.testers.soroosh.BsUtil;

public class BsUtilTests {
    BsUtil bsUtil = new BsUtil();
    @Test
    public void isValidDirectoryFail()
    {
        assertFalse( bsUtil.isValidDirectory("sacasscs"));
    }

    @Test
    public void isValidDirectoryFail2()
    {
        assertFalse( bsUtil.isValidDirectory("/Users/randomtester"));
    }

    @Test
    public void isValidDirectoryPass()
    {
        assertTrue( bsUtil.isValidDirectory("/Users/sorooshavazkhani"));
    }
}