package com.bsep.tim11.bseptim11.common;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class TimeProvider implements Serializable {

    private static final long serialVersionUID = 60760671214376643L;

    public Date now() {
        return new Date();
    }

}
