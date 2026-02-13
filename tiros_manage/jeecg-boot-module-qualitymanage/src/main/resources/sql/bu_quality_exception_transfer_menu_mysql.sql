-- 质量管理-例外转序 菜单与角色授权（MySQL）
-- 说明：按实际环境调整 ROLE_ID；默认示例为管理员角色

-- 1) 菜单（父节点按已存在质量目录自动挂载）
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect,
  menu_type, perms, perms_type, sort_no, always_show, icon,
  is_route, is_leaf, keep_alive, hidden,
  description, create_by, create_time, update_by, update_time,
  del_flag, rule_flag, status, internal_or_external
)
SELECT
  'tiros_quality_exception_transfer',
  COALESCE(
    (SELECT id FROM sys_permission WHERE url = '/tiros/quality' LIMIT 1),
    COALESCE(
      (SELECT id FROM sys_permission WHERE name = '质量管理' LIMIT 1),
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
  '质量例外转序管理', 'admin', NOW(), 'admin', NOW(),
  0, 0, '1', 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 'tiros_quality_exception_transfer');

-- 2) 角色授权（可按需复制给更多角色）
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids)
SELECT REPLACE(UUID(), '-', ''), 'f6817f48af4fb3af11b9e8bf182f618b', 'tiros_quality_exception_transfer', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_permission
  WHERE role_id = 'f6817f48af4fb3af11b9e8bf182f618b'
    AND permission_id = 'tiros_quality_exception_transfer'
);
