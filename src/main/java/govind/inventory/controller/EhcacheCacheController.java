package govind.inventory.controller;

import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.ehcache.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class EhcacheCacheController {
	@Resource
	private ICacheService cacheService;

	@PutMapping("/cache")
	public void testPutCache(@RequestBody ProductInfo info) {
		log.info("保存{}到本地ehcache缓存", cacheService.saveProductInfoToLocalCache(info));
	}

	@GetMapping("/cache/{id:\\d+}")
	public ProductInfo getCache(@PathVariable("id") long id) {
		ProductInfo cache = cacheService.getProductInfoFromLocalCache((int) id);
		log.info("本地缓存id={}结果:{}", id, cache);
		return cache;
	}
}
