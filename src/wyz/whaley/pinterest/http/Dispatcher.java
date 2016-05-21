package wyz.whaley.pinterest.http;

import java.io.InputStream;

public interface Dispatcher {
    void dispatcherResult(final InputStream in);
}
