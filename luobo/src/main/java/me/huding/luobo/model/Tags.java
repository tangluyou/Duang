package me.huding.luobo.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;

import me.huding.luobo.model.base.BaseTags;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Tags extends BaseTags<Tags> {
	public static final Tags dao = new Tags();
	
	
	public static List<Tags> findAll(){
		String tableName = TableMapping.me().getTable(Tags.class).getName();
		String sql = "select * from " + tableName;
		return dao.find(sql);
	}
	
	public static Page<Record> paginate(int pageNum, int pageSize) {
		String select = "select *";
		String suffix = "from tags";
		Page<Record> page = Db.paginate(pageNum, pageSize, select, suffix);
		if(page.getList().isEmpty()){
			return page;
		}
		StringBuilder builder = new StringBuilder("select tagID,count(*) as blogNum from blog_tags where tagID in (");
		boolean isFirst = true;
		for(Record record : page.getList()){
			if(!isFirst){
				builder.append(",");
			} 
			builder.append("'").append(record.get("id")).append("'");
			isFirst = false;
		}
		builder.append(") group by tagID");
		List<Record> counts = Db.find(builder.toString());
		Map<String, Object> map = new HashMap<String,Object>();
		for(Record record : counts){
			map.put(record.getStr("tagID"), record.get("blogNum"));
		}
		for(Record record : page.getList()){
			String tagID = record.getStr("id");
			Object blogNum = map.get(tagID);
			if(blogNum == null){
				record.set("blogNum",0);
			} else {
				record.set("blogNum",blogNum);
			}
		}
		return page;
	}
}
