-- TIROS基础管理菜单配置
-- 说明：如果工艺电子手册菜单已存在，请删除对应的INSERT语句

-- 1. 创建父菜单：TIROS基础管理
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_basic_manage_parent',           -- ID
  NULL,                                   -- PARENT_ID (一级菜单，父ID为空)
  'TIROS基础管理',                       -- NAME
  '/tiros/craft',                        -- URL
  'layouts/RouteView',                   -- COMPONENT (一级菜单使用RouteView)
  NULL,                                  -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  0,                                     -- MENU_TYPE (0-一级菜单)
  NULL,                                  -- PERMS
  NULL,                                  -- PERMS_TYPE
  100,                                   -- SORT_NO (排序号，可根据需要调整)
  0,                                     -- ALWAYS_SHOW
  'tool',                                -- ICON (图标，可根据需要调整)
  1,                                     -- IS_ROUTE
  0,                                     -- IS_LEAF (不是叶子节点)
  0,                                     -- KEEP_ALIVE
  0,                                     -- HIDDEN
  'TIROS基础数据管理',                   -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 2. 生产通知单
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_production_notice',             -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '生产通知单',                          -- NAME
  '/tiros/craft/production-notice',      -- URL
  'tiros/basic/ProductionNotice',        -- COMPONENT
  'ProductionNotice',                    -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  1,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'file-text',                           -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '生产通知单管理',                      -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 3. 质量可视化
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_quality_process',               -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '质量可视化',                          -- NAME
  '/tiros/craft/quality-process',        -- URL
  'tiros/basic/QualityProcess',          -- COMPONENT
  'QualityProcess',                      -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  2,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'bar-chart',                           -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '质量管理可视化',                      -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 4. 定额BOM
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_quota_bom',                     -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '定额BOM',                             -- NAME
  '/tiros/craft/quota-bom',              -- URL
  'tiros/basic/QuotaBom',                -- COMPONENT
  'QuotaBom',                            -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  3,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'database',                            -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '定额BOM管理',                         -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 5. 标准工序
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_standard_process',              -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '标准工序',                            -- NAME
  '/tiros/craft/standard-process',       -- URL
  'tiros/basic/StandardProcess',         -- COMPONENT
  'StandardProcess',                     -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  4,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'ordered-list',                        -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '标准工序管理',                        -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 6. 工艺电子手册
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_tech_manual',                   -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '工艺电子手册',                        -- NAME
  '/tiros/craft/tech-manual',            -- URL
  'tiros/basic/TechManual',              -- COMPONENT
  'TechManual',                          -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  5,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'book',                                -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '工艺电子手册管理',                    -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

-- 7. 作业指导书
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_job_guide_book',                -- ID
  'tiros_basic_manage_parent',           -- PARENT_ID
  '作业指导书',                          -- NAME
  '/tiros/craft/job-guide-book',         -- URL
  'tiros/basic/JobGuideBook',            -- COMPONENT
  'JobGuideBook',                        -- COMPONENT_NAME
  NULL,                                  -- REDIRECT
  1,                                     -- MENU_TYPE (1-子菜单)
  NULL,                                  -- PERMS
  '1',                                   -- PERMS_TYPE
  6,                                     -- SORT_NO
  0,                                     -- ALWAYS_SHOW
  'file-done',                           -- ICON
  1,                                     -- IS_ROUTE
  1,                                     -- IS_LEAF (叶子节点)
  1,                                     -- KEEP_ALIVE (缓存页面)
  0,                                     -- HIDDEN
  '作业指导书管理',                      -- DESCRIPTION
  'admin',                               -- CREATE_BY
  SYSDATE,                               -- CREATE_TIME
  'admin',                               -- UPDATE_BY
  SYSDATE,                               -- UPDATE_TIME
  0,                                     -- DEL_FLAG
  0,                                     -- RULE_FLAG
  '1',                                   -- STATUS
  0                                      -- INTERNAL_OR_EXTERNAL
);

COMMIT;
