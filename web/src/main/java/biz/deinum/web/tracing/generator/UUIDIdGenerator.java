package biz.deinum.web.tracing.generator;

import biz.deinum.web.tracing.IdGenerator;

import javax.servlet.ServletRequest;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 6-12-13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class UUIDIdGenerator implements IdGenerator {

    @Override
    public String generate(ServletRequest request) {
        return UUID.randomUUID().toString();
    }
}
