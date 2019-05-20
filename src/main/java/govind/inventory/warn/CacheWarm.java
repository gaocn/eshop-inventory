package govind.inventory.warn;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.ehcache.ICacheService;
import govind.inventory.lock.ZookeeperSession;
import govind.inventory.product.SpringContext;

public class CacheWarm extends Thread {
	private ICacheService cacheService = (ICacheService) SpringContext.getWebAppCtx().getBean("cacheService");
	@Override
	public void run() {
		//1. 获取storm task id 列表
		String taskIdList = ZookeeperSession.getInstance().getZnodeData("/taskid-list");
		//2. 若taskid不为空，
		if (taskIdList != null && !"".equals(taskIdList)) {
			String[] taskIds = taskIdList.split(",");
			for (String taskid : taskIds) {
				String taskIdLockPath = "/taskid-lock-" + taskid;
				boolean acquired = ZookeeperSession.getInstance().acquireFastFailDistributedLock(taskIdLockPath);
				if (!acquired) {
					continue;
				}
				//已拿到数据，检查预热的状态
				String taskIdStatusLockPath = "/taskid-status-lock-" + taskid;
				ZookeeperSession.getInstance().acquireLock(taskIdStatusLockPath);

				String taskIdStatus = ZookeeperSession.getInstance().getZnodeData("/taskid-status-" + taskid);
				if (taskIdStatus == null) {
					//缓存尚未被预热，执行预热操作
					String productList = ZookeeperSession.getInstance().getZnodeData("/task-hot-product-list-" + taskid);
					JSONArray productidArr = JSONArray.parseArray(productList);
					for (int i = 0; i < productidArr.size(); i++) {
						String kv = productidArr.getString(i);
						Long productid = Long.parseLong(kv.substring(1, kv.indexOf(":")));
						//模拟从MySQL中拉取到的数据
						String productInfoJson = "{\"id\":" + productid + ",\"name\":\"iphone8手机\", \"price\":6999,\"pictures\":\"a.jpg,b.jpg\",\"specification\":\"iphone8规格\",\"service\":\"售后服务\", \"color\":\"black\", \"size\":\"5.5\",\"shopId\":1,\"modifiedTime\":\"2019-05-17 20:00:00\"}";
						ProductInfo productInfo = JSONObject.parseObject(productInfoJson, ProductInfo.class);
						cacheService.saveProductInfoToLocalCache(productInfo);
						cacheService.saveProductInfoToRedisCache(productInfo);
					}
					//设置预热的状态，防止重复预热
					ZookeeperSession.getInstance().setZnodeData(taskIdStatusLockPath, "success");
				}
				ZookeeperSession.getInstance().releaseLock(taskIdStatusLockPath);
				ZookeeperSession.getInstance().releaseDistributedLock(taskIdLockPath);
			}
		}
	}
}
