package org.jxmpp.stringprep;

import org.jxmpp.stringprep.simple.SimpleXmppStringprep;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.LruCache;

public class XmppStringPrepUtil {

	static {
		// Ensure that there is always at least the simple XMPP stringprep implementation active
		SimpleXmppStringprep.setup();
	}

	private static final Cache<String, String> NODEPREP_CACHE = new LruCache<>(100);
	private static final Cache<String, String> DOMAINPREP_CACHE = new LruCache<>(100);
	private static final Cache<String, String> RESOURCEPREP_CACHE = new LruCache<>(100);

	private static XmppStringprep xmppStringprep;

	public static void setXmppStringprep(XmppStringprep xmppStringprep) {
		XmppStringPrepUtil.xmppStringprep = xmppStringprep;
	}

	public static String localprep(String string) throws XmppStringprepException {
		if (xmppStringprep == null) {
			return string;
		}
		// Avoid cache lookup if string is the empty string
		throwIfEmptyString(string);
		String res = NODEPREP_CACHE.get(string);
		if (res != null) {
			return res;
		}
		res = xmppStringprep.localprep(string);
		NODEPREP_CACHE.put(string, res);
		return res;
	}

	public static String domainprep(String string) throws XmppStringprepException {
		if (xmppStringprep == null) {
			return string;
		}
		// Avoid cache lookup if string is the empty string
		throwIfEmptyString(string);
		String res = DOMAINPREP_CACHE.get(string);
		if (res != null) {
			return res;
		}
		res = xmppStringprep.domainprep(string);
		DOMAINPREP_CACHE.put(string, res);
		return res;
	}

	public static String resourceprep(String string) throws XmppStringprepException {
		if (xmppStringprep == null) {
			return string;
		}
		// Avoid cache lookup if string is the empty string
		throwIfEmptyString(string);
		String res = RESOURCEPREP_CACHE.get(string);
		if (res != null) {
			return res;
		}
		res = xmppStringprep.resourceprep(string);
		RESOURCEPREP_CACHE.put(string, res);
		return res;
	}

	public static void setMaxCacheSizes(int size) {
		NODEPREP_CACHE.setMaxCacheSize(size);
		DOMAINPREP_CACHE.setMaxCacheSize(size);
		RESOURCEPREP_CACHE.setMaxCacheSize(size);
	}

	/**
	 * Throws a XMPP Stringprep exception if string is the empty string.
	 *
	 * @param string String
	 * @throws XmppStringprepException Exception
	 */
	private static void throwIfEmptyString(String string) throws XmppStringprepException {
		if (string.length() == 0) {
			throw new XmppStringprepException(string, "Argument can't be the empty string");
		}
	}
}
