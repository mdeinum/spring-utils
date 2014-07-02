package biz.deinum.web.tracing;

import javax.servlet.ServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 16-9-13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public interface IdRepository {

    String createOrGet(ServletRequest request);
}
