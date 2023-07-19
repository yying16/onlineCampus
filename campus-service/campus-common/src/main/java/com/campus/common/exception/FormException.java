package com.campus.common.exception;

/**
 * author yying
 */
public class FormException extends Exception{

    public FormException(String msg){
        super("表单为空");
    }
}
