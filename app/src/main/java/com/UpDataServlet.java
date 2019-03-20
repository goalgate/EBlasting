//package com;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.math.BigDecimal;
//import java.sql.Blob;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.management.Query;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.log4j.Logger;
//
//
//import cn.cbdi.yzb.cjy.base.CjyUtil;
//import cn.cbsw.cjy.tools.Format;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
//
//
//@SuppressWarnings({"unchecked"})
//public class UpDataServlet extends HttpServlet {
//	private String path="D:\\test\\pic\\";
//
//
//    private static final long serialVersionUID = 1L;
//	private static Logger logger = Logger.getLogger(UpDataServlet.class);
//
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		this.doGet(req, resp);
//	}
//
//	public void destroy() {
//		super.destroy();
//	}
//
//	public  String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("x-forwarded-for");
//        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
//
//
//
//	//�ϴ����ݲ����ǣ�request.getParameter("jsonData")
//	//ͼƬ���ݶ�Ϊbase64��
//	/*result���ز���:
//	 * true:�����ɹ�
//	 * false:����ʧ��
//	 * dataErr:�ϴ��Ĳ����д�
//	 * keyFalse:key��������ȷ
//	 * dbErr:���ݿ����ʧ��
//	 */
//
//	public void doGet(HttpServletRequest request, HttpServletResponse response)
//
//			throws ServletException, IOException {
//		String data = "{result:'dataErr'}";	// ���ص�����
//		try {
//			request.setCharacterEncoding("UTF-8");
//			response.setContentType("text/html;charset=UTF-8");
//			String dataType = request.getParameter("dataType"); //�ӿڷ�������
//			String key = request.getParameter("key"); //������
//			String daId = "";//�豸ID
//			try {
//
//				if (!"true".equals(CjyUtil.isKey(key))) {//У��key
//					data = CjyUtil.toResult(CjyUtil.isKey(key));
//				}else{
//					daId = CjyUtil.getDaid(key); // ȡ�豸ID
//					//System.out.println(new java.util.Date().toLocaleString()+"_daId:"+daId);
//					if(daId==null || "".equals(daId)){
//						data = "{result:'dataErr'}";
//					}else{
//
//						if(dataType.equals("login")) //��¼ϵͳ
//						{
//							data=login(request);
//						}else if(dataType.equals("testNet")) //�������   �������ӵ��������Ƿ�����
//						{
//							data="{result:'true'}";
//						}else if(dataType.equals("online")) //�豸����  ���ڼ�¼�豸��������     ��ʱ���� 1Сʱ1��
//						{
//							data=online();
//						}else if(dataType.equals("queryPersonInfo")) //��ϵͳ��ѯ��ҵ��Ա��Ϣ���鵽�ſɲɼ���Ա��Ϣ��������Ա����
//						{
//							data=queryPersonInfo(request);
//						}else if(dataType.equals("personInfo")) //��ҵ��Ա��Ϣ�ɼ�
//						{
//							data=personInfo(request);
//						}else if(dataType.equals("work")) //��ҵ��Ա����
//						{
//							data=work(request);
//						}else if(dataType.equals("workRecord")) //ֵ���ռ�
//						{
//							data=workRecord(request);
//						}else if(dataType.equals("checkDevice")) //�豸�ճ����
//						{
//							data=checkDevice(request);
//						}else if(dataType.equals("outsidePerson")) //������Ա��¼
//						{
//							data=outsidePerson(request);
//						}
//						else if(dataType.equals("manageCheck")) //�����ż��
//						{
//							data=manageCheck(request);
//						}
//					}
//				}
//			} catch (Exception e) {
//				data = "{result:'dbErr'}";
//				e.printStackTrace();
//				logger.error(e);
//			}
//		}catch (Exception ex) {
//			ex.printStackTrace();
//			logger.error(ex);
//		}
//
//		PrintWriter out = response.getWriter();
//		try {
//			out.print(data);
//			out.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//		} finally {
//			try {
//				out.close();
//			} catch (Exception ee) {
//				ee.printStackTrace();
//				logger.error(ee);
//			}
//		}
//	}
//
///*��¼ϵͳ
// * �ϴ����� {username:'�û���',password:'����',daid:'�豸ID'}
// * ����result,�ɹ����أ�{result:"true",id:'ϵͳID'}
// */
//public String login(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println("login:�û�����"+resultVo.get("username")+
//						"_���룺"+resultVo.get("password")+"_�豸ID��"+resultVo.get("daid"));
//				if(false)
//				{
//					return "{result:'true',id:'test12345'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		//result = "";	//�ϴ���json�����д�
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
///*�豸����  ���ڼ�¼�豸��������     ��ʱ���� 1Сʱ1��
// * ����result
// */
//public String online()
//{	String data="{result:'dbErr'}";
//	try{
//		//������
//		data="{result:'true'}";
//	}catch(Exception e){
//		data="{result:'dbErr'}";
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
///*��ϵͳ��ѯ��ҵ��Ա��Ϣ���鵽�ſɲɼ���Ա��Ϣ��������Ա����
// * �ϴ�����:{id:'ϵͳID',identity:'��ݺ�'}
// * ����result �ɹ����أ�{result:"true",personType:'��Ա����'}
// */
//public String queryPersonInfo(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println("queryPersonInfo:ϵͳID��"+resultVo.get("id")+
//						"_��ݺţ�"+resultVo.get("identity"));
//				if(true)
//				{
//					return "{result:'true',personType:'1'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		//result = "";	//�ϴ���json�����д�
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
///*�ɼ���ҵ��Ա��Ϣ
// * �ϴ�����:{id:'ϵͳID','name:'����',identity:'��ݺ�',personType:'��Ա����',personHPhoto:'��Աͷ�����֤��',
//    personPhoto:'��Ա��Ƭ',fingerprint:'ָ����֤��base64',fingerprintPhoto:'ָ��ͼƬ'}
// * ����result
// */
//public String personInfo(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println("personInfo:ϵͳID��"+resultVo.get("id")+	"_������"+resultVo.get("name")+"_��ݺţ�"+resultVo.get("identity")
//						+"_��Ա����:"+resultVo.get("personType"));
//				System.out.println("��Աͷ��:"+resultVo.get("personHPhoto"));
//				System.out.println("��Ա��Ƭ:"+resultVo.get("personPhoto"));
//				System.out.println("ָ����֤��:"+resultVo.get("fingerprint"));
//				System.out.println("ָ����ͼƬ:"+resultVo.get("fingerprintPhoto"));
//				CjyUtil.saveJpg(path,resultVo.get("identity")+"_1.jpg",Format.fromBase64(resultVo.get("personHPhoto")));
//				CjyUtil.saveJpg(path,resultVo.get("identity")+"_2.jpg",Format.fromBase64(resultVo.get("personPhoto")));
//				CjyUtil.saveJpg(path,resultVo.get("identity")+"_3.jpg",Format.fromBase64(resultVo.get("fingerprintPhoto")));
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
//
///*��ҵ��Ա����
// * �ϴ�����:{id:'ϵͳID','name:'����',identity:'��ݺ�',headPhoto:'���֤��Ƭbase64��',photo:'��Ƭbase64��',workType:'���°����ͣ��ϰ�/�°ࣩ',time:'2018-12-05 11:19:02',personPhoto:'��Ա��Ƭ'}
// * ����result
// */
//public String work(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println("personInfo:ϵͳID��"+resultVo.get("id")+	"_������"+resultVo.get("name")+"_��ݺţ�"+resultVo.get("identity")
//						+"_���°�����:"+resultVo.get("workType")+"_ʱ��:"+resultVo.get("time"));
//				System.out.println("��Աͷ��:"+resultVo.get("personHPhoto"));
//				CjyUtil.saveJpg(path,resultVo.get("identity")+"_work.jpg",Format.fromBase64(resultVo.get("personHPhoto")));
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
///*ֵ���ռ�    ֵ������   ��ѡ��д  ��ѡ����Ϊ�������20����ͬ��
// * �ϴ�����:{id:'ϵͳID','name:'ֵ��������',identity:'��ݺ�',headPhoto:'���֤��Ƭbase64��',photo:'��Ƭbase64��',state:'ֵ������״̬������/�쳣��',content:'ֵ������',time:'2018-12-05 11:19:02'}
// * ����result
// */
//public String workRecord(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println("personInfo:ϵͳID��"+resultVo.get("id")+	"_������"+resultVo.get("name")+"_��ݺţ�"+resultVo.get("identity")
//						+"_ֵ������״̬:"+resultVo.get("state")+"_ֵ������:"+resultVo.get("content")+"_ʱ��:"+resultVo.get("time"));
//
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//
//}
//
//
///*�豸���   �������    ��ѡ��д  ��ѡ����Ϊ�������20����ͬ��
// * �ϴ���������:{id:'ϵͳID',name:'���������',identity:'���֤��',headPhoto:'���֤��Ƭbase64��',photo:'��Ƭbase64��',time:'2019-03-05 11:19:02�����ʱ�䣩'}||[{devName:'�豸��1',state:'�豸״̬(����/�쳣)',content:'�������'},[{devName:'�豸��2',state:'�豸״̬(����/�쳣)',content:'�������'}]
// * * ����result
// */
//public String checkDevice(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	//String jsonDatas=jsonData.split("||");
//
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println(jsonData);
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//}
//
//
///*�����ż���¼     ����
// * �ϴ���������:{id:'ϵͳID',name:'���������',identity:'���֤��',time:'2019-03-05 11:19:02�����ʱ�䣩'}'}]
// * * ����result
// */
//public String manageCheck(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	//String jsonDatas=jsonData.split("||");
//
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println(jsonData);
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//}
//
//
//
//
///*������Ա��¼     �������� ��ѡ��д  ��ѡ����Ϊ�������20����ͬ���͵ģ�Ĭ��ֵΪ���һ�����Ա���ټ�¼����
// * �ϴ���������:{id:'ϵͳID',name:'����������',identity:'���֤��',affair:'��������',headPhoto:'���֤��Ƭbase64��',photo:'��Ƭbase64��',time:'2019-03-05 11:19:02�����ʱ�䣩'}'}]
// * * ����result
// */
//public String outsidePerson(HttpServletRequest request)
//{
//	String jsonData = StringEscapeUtils.unescapeHtml(request.getParameter("jsonData"));
//	//String jsonDatas=jsonData.split("||");
//
//	Map<String, String> resultVo = null;
//	String data="{result:'dataErr'}";
//	try{
//
//		resultVo = new Gson().fromJson(jsonData, new TypeToken<HashMap<String,String>>(){}.getType());
//		if (resultVo != null) {
//			try{
//				System.out.println(jsonData);
//				if(true)
//				{
//					return "{result:'true'}";
//				}else
//				{
//					return "{result:'false'}";
//				}
//
//			}catch(Exception e1)
//			{
//				data="{result:'dbErr'}";
//				e1.printStackTrace();
//				logger.error(e1);
//			}
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		logger.error(e);
//	}
//	return data;
//}
//
//
//}
