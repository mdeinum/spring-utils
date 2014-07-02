package biz.deinum.web.tracing;

import javax.servlet.ServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 6-12-13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public interface IdGenerator {

    String generate(ServletRequest request);

}
