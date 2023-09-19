package com.softknife.config;

import org.aeonbits.owner.Tokenizer;

/**
 * @author Sasha Matsaylo on 10/18/21
 * @project demo-restapi-test
 */
public class CommaTokenizer implements Tokenizer {

    // this logic can be as much complex as you need
    @Override
    public String[] tokens(String values) {
        return values.replaceAll("\\s","").split(",", -1);
    }
}
