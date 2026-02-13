-- 质量管理-例外转序 菜单与角色授权（Oracle）
-- 说明：按实际环境调整 ROLE_ID；默认示例为管理员角色

-- 1) 菜单（父节点按已存在质量目录自动挂载）
INSERT INTO SYS_PERMISSION (
  ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, REDIRECT,
  MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON,
  IS_ROUTE, IS_LEAF, KEEP_ALIVE, HIDDEN,
  DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME,
  DEL_FLAG, RULE_FLAG, STATUS, INTERNAL_OR_EXTERNAL
)
SELECT
  'tiros_quality_exception_transfer',
  NVL(
    (SELECT id FROM sys_permission WHERE url = '/tiros/quality' AND rownum = 1),
    NVL(
      (SELECT id FROM sys_permission WHERE name = '质量管理' AND rownum = 1),
      'tiros_basic_manage_parent'
    )
  ),
  '例外转序',
  '/tiros/quality/exception-transfer',
  'tiros/quality/exceptiontransfer/ExceptionTransferPage',
  'ExceptionTransferPage',
  NULL,
  1, NULL, '1', 96, 0, 'swap',
  1, 1, 1, 0,
  '质量例外转序管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 'tiros_quality_exception_transfer');

-- 2) 角色授权（可按需复制给更多角色）
INSERT INTO SYS_ROLE_PERMISSION (ID, ROLE_ID, PERMISSION_ID, DATA_RULE_IDS)
SELECT REPLACE(SYS_GUID(), '-', ''), 'f6817f48af4fb3af11b9e8bf182f618b', 'tiros_quality_exception_transfer', NULL
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM SYS_ROLE_PERMISSION
  WHERE ROLE_ID = 'f6817f48af4fb3af11b9e8bf182f618b'
    AND PERMISSION_ID = 'tiros_quality_exception_transfer'
);

COMMIT;

