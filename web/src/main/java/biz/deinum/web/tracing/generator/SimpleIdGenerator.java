package biz.deinum.web.tracing.generator;

import biz.deinum.web.tracing.IdGenerator;

import javax.servlet.ServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 6-12-13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class SimpleIdGenerator implements IdGenerator {

    @Override
    public String generate(ServletRequest request) {
        return String.valueOf(request.hashCode());
    }
}
