package govind.inventory.controller;

import govind.inventory.dao.entity.ProductInventory;
import govind.inventory.request.ProductInventoryCacheReloadRequest;
import govind.inventory.request.ProductInventoryDBDataUpdateRequest;
import govind.inventory.service.IProductInventoryService;
import govind.inventory.service.RequestAsyncProcessorServiceImpl;
import govind.inventory.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品库存控制器
 */
@Slf4j
@RestController
public class ProductInventoryController {
	@Autowired
	private IProductInventoryService productInventoryService;
	@Autowired
	private RequestAsyncProcessorServiceImpl requestAsyncProcessorService;

	/**
	 * 更新商品库存
	 */
	@PostMapping("/updateProductInventory")
	@ResponseBody
	public Response updateProductInventory(ProductInventory productInventory) {
		try {
			ProductInventoryDBDataUpdateRequest request = new ProductInventoryDBDataUpdateRequest(productInventory, productInventoryService);
			requestAsyncProcessorService.process(request);
		} catch (Exception e) {
			Response.fail(e.getMessage());
			log.error("出错：{}", e.getMessage());
		}
		return Response.success();
	}

	/**
	 * 获取商品库存
	 */
	@GetMapping("/getProductInventory/{id:\\d+}")
	@ResponseBody
	public Response getProductInventory(@PathVariable("id") Integer productId) {
		try {
			ProductInventoryCacheReloadRequest request = new ProductInventoryCacheReloadRequest(productInventoryService, productId);
			requestAsyncProcessorService.process(request);
			//将请求扔给service异步处理后，就需要while(true)一会，在这里hang住，
			// 去尝试等待前面有商品库存更新的操作，同时缓存刷新的操作将最新数据刷新
			// 到缓存中
			long startTime = System.currentTimeMillis();
			long endTime = 0L;
			long waitTime = 0L;
			//等待操作200ms没有从缓存中获取结果，则直接尝试从数据库中读取数据
			while (true) {
				//若等待了200毫秒还没有更新缓存，则直接退出
				if (waitTime > 200) {
					break;
				}
				//尝试从redis中读取商品库存的缓存数据
				ProductInventory inventory = productInventoryService.getProductInventoryCache(productId);
				//若读取到数据则直接返回
				if(inventory != null) {
					return Response.success(inventory);
				} else {
					//等待20ms
					endTime = System.currentTimeMillis();
					waitTime = endTime - startTime;
				}
			}
			//直接从数据库中读取数据
			ProductInventory productInventory = productInventoryService.findProductInventory(productId);
			if (productInventory != null) {
				//刷新缓存
				//productInventoryService.setProductInventoryCache(productInventory);
				//代码运行到这里有三种情况：1、上一次是读请求，数据刷入redis但被LRU
				// 算法去清理了，标志位为false，所以下一次读请求从缓存是拿不到数据的，
				// 再放入一个读Request进队列，让数据刷新进入。2、可能在200ms内读请
				// 求在队列中一直积压没有等待到它执行(生产环境遇到这个问题要么扩容要
				// 么提高数据库的处理速度)就会直接查询数据库然后给队列塞进去一个刷新
				// 的请求会被读请求去重给去掉！3、数据库本身没有请求数据，缓存穿透直
				// 接请求MySQL。
				ProductInventoryCacheReloadRequest request1 = new ProductInventoryCacheReloadRequest(productInventoryService, productId, true);
				requestAsyncProcessorService.process(request1);

				return Response.success(productInventory);
			}
		} catch (Exception e) {
			Response.fail(e.getMessage());
			log.error("出错：{}", e.getMessage());
		}
		return Response.fail("没有读取到productId="+ productId + "的库存信息");
	}
}
