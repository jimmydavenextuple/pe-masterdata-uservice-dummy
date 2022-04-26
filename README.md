# Common Near Cache uService
    This libaray provides abstraction and generic implementation for near-cache.


#####  Following are modules that are present in this repo
- Abstraction layer `core-nearcache` that would be used as a soft dependency in the repos that need cache service
- A generic implementation `common-nearcache` module that would then be used by different cache implementation
- An abstract cache implementation `common-nearcache-spring` module using `Spring Cache` is available as a module
    - Using spring cache gives the flexibility to implement different cache storages such as Caffeine, Redis
    - Spring cache is `JSR-107` (JCache) compliant
    - By default, Caffeine is the chosen cache storage
    - Consumers of this module do have a choice to override by excluding the `com.github.ben-manes.caffeine: caffeine` and include appropriate dependency
- Master data cache domain objects and mappers `tenant-nearcache-domain` and `node-nearcache-domain` modules for tenant and node services
- Master data cache common service implementation `tenant-nearcache-userservice` and `node-nearcache-userservice` modules for tenant and node services
- Master data cache impl modules with spring implementation for above interfaces
    - `tenant-nearcache-spring-userservice` and `node-nearcache-spring--userservice` modules for tenant and node services
    -
    - these modules extend `common-nearcache-spring` in the context of tenant/node master data keys and values replacing generics
    -
#### Following are the bunch of things a consuming repository should be doing
- Include `core-nearcache` library dependency in the controller layer that gives the cache service interface
- Include master data cache domain libraries `tenant-nearcache-domain` and `node-nearcache-domain` modules that would give access to respective cache key and values
- A new module that would depend on
- 