package cn.mulanbay.common.exception;

/**
 * 全局的错误代码
 * 可以使用的代码段：0-99，10000-19999
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class ErrorCode {

	// 系统级别 start
	public final static int SUCCESS = 0;

	public final static int OBJECT_GET_ERROR = 1;

	public final static int OBJECT_ADD_ERROR = 2;

	public final static int OBJECT_UPDATE_ERROR = 3;

	public final static int OBJECT_DELETE_ERROR = 4;

	public final static int OBJECT_GET_LIST_ERROR = 5;

	public final static int QUERY_BUILD_ERROR = 6;

	public final static int HIBERNATE_EXCEPTION = 7;

	public final static int SYSTEM_ERROR = 8;

	public final static int UNKNOWN_ERROR = 9;

	public final static int PARAMETER_DUPLICATE_CAL = 10;

	public final static int HTTP_ERROR =11;

	public final static int DO_BUSS_ERROR =12;

	public final static int SYSTEM_START =13;

	public final static int NULL_POINT_EXCEPTION =14;

	// 系统级别 end
	public final static int FORM_VALID_ERROR =15;

	public final static int THREAD_CAN_ONLY_DO_ONCE =16;

	public final static int GET_CACHE_ERROR =17;

	public final static int CMD_EXEC_NOTIFY =18;

	public final static int CMD_EXEC_ERROR =19;

	public final static int JSON_PARSE_ERROR =20;

	public final static int FILE_PATH_NOT_EXIT = 21;

}
