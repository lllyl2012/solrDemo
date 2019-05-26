package com.example.demo;

import com.example.demo.domain.QltService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.StringUtils;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void saveOrUpdate() throws IOException, SolrServerException {
		//获得连接
		String solfUrl = "http://localhost:8080/solr/core_demo";
		HttpSolrClient client = new HttpSolrClient.Builder(solfUrl).build();
		//创建对象
		QltService qltService = new QltService();
		qltService.setQlInnerCode("12sdfwe23");
		qltService.setQlName("恰饭1");
		qltService.setBelongxiaqucode(320122.0);
		qltService.setDeptName("gaga");
		//添加对象
		UpdateResponse response = client.addBean(qltService);
		//提交对象
		client.commit();
		//关闭连接
		client.close();
	}

	//删除索引
	@Test
	public void delIndex() throws IOException, SolrServerException {
		//获得连接
		String solfUrl = "http://localhost:8080/solr/core_demo";
		HttpSolrClient client = new HttpSolrClient.Builder(solfUrl).build();
		//删除对象
		client.deleteById("12sdfwe23");
//		client.deleteByQuery("*:*");清空索引库的最快方式
		//提交对象
		client.commit();
		//关闭连接
		client.close();
	}

	//查询solr
	@Test
	public void simpleQuery() throws IOException, SolrServerException {
		//获得连接
		String solfUrl = "http://localhost:8080/solr/core_demo";
		HttpSolrClient client = new HttpSolrClient.Builder(solfUrl).build();
		//查询对象
		String q = "*:*";
		SolrParams query = new SolrQuery(q);
		QueryResponse queryResponse = client.query(query);

		List<QltService> qltServiceList  = queryResponse.getBeans(QltService.class);

		qltServiceList.forEach(System.out::println);

		//提交对象
		client.commit();
		//关闭连接
		client.close();
	}

	//复杂查询
	@Test
	public void fuzaQuery() throws IOException, SolrServerException {
		//获得连接
		String solfUrl = "http://localhost:8080/solr/core_demo";
		HttpSolrClient client = new HttpSolrClient.Builder(solfUrl).build();
		//查询对象

		SolrQuery query = new SolrQuery();

		//1设置条件
		String keyword = "建设";
		if (StringUtils.isEmpty(keyword)) {
			query.set("q", "*:*");
		}else {
			query.set("q", "QL_NAME:" + keyword);
		}
		//2.1设置过滤条件fq
		String catalogName = "台州湾集聚区";
		if (!StringUtils.isEmpty(catalogName)) {
			query.addFilterQuery("DEPT_NAME:" + catalogName);
		}
		//2.2范围筛选
//		String priceStr = "320000.0-331000.0";
		String priceStr = "331000.0-";//[331000.0 TO *]
		if (!StringUtils.isEmpty(priceStr)) {
			String[] arrs = priceStr.split("-");
			if(arrs.length == 1){
				query.addFilterQuery("BELONGXIAQUCODE:[" + arrs[0] + " TO *]");
			}else {
				String prefix = arrs[0];
				if(StringUtils.isEmpty(arrs[0])){
					prefix = "*";
				}
				query.addFilterQuery("BELONGXIAQUCODE:[" + prefix + " TO " + arrs[1] + "]");
			}
		}

		//3排序
		int psort = 0;
		if(psort == 1){
			query.addSort("BELONGXIAQUCODE", SolrQuery.ORDER.asc);
		} else if (psort == 2) {
			query.addSort("BELONGXIAQUCODE", SolrQuery.ORDER.desc);
		}

		//4分页功能
		query.setStart(0);
		query.setRows(10);

		//5回显
		query.setFields("QL_NAME");

		//6默认域
		query.set("df","QL_NAME");

		//7高亮设置
		query.setHighlight(true);
		query.addHighlightField("QL_NAME");
		query.setHighlightSimplePre("<font color='red'>");
		query.setHighlightSimplePost("</font>");

		QueryResponse queryResponse = client.query(query);

		List<QltService> qltServiceList  = queryResponse.getBeans(QltService.class);

		//得到高亮数据
		Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
		qltServiceList.forEach(i->{
			String id = i.getQlInnerCode();
			Map<String, List<String>> map1 = map.get(id);
			List<String> map2 = map1.get("QL_NAME");

			System.out.println(map2!=null?map2.get(0):i.getQlName());
		});

		//提交对象
		client.commit();
		//关闭连接
		client.close();
	}
}
