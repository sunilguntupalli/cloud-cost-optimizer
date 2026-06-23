terraform {
  required_version = ">= 1.6.0"
  required_providers { azurerm = { source = "hashicorp/azurerm", version = "~> 3.100" } }
}
provider "azurerm" { features {} }

variable "location" { type = string default = "eastus" }
variable "prefix" { type = string default = "finopslens" }

resource "azurerm_resource_group" "main" { name = "${var.prefix}-rg" location = var.location }
resource "azurerm_container_registry" "main" { name = "${replace(var.prefix, "-", "")}acr" resource_group_name = azurerm_resource_group.main.name location = var.location sku = "Basic" admin_enabled = false }
resource "azurerm_kubernetes_cluster" "main" {
  name = "${var.prefix}-aks" location = azurerm_resource_group.main.location resource_group_name = azurerm_resource_group.main.name dns_prefix = var.prefix
  default_node_pool { name = "system" node_count = 2 vm_size = "Standard_B2s" }
  identity { type = "SystemAssigned" }
}
resource "azurerm_postgresql_flexible_server" "main" {
  name = "${var.prefix}-postgres" resource_group_name = azurerm_resource_group.main.name location = azurerm_resource_group.main.location version = "16"
  administrator_login = "costadmin" administrator_password = "ChangeThisInTfvars123!" sku_name = "B_Standard_B1ms" storage_mb = 32768
}
output "acr_login_server" { value = azurerm_container_registry.main.login_server }
output "aks_name" { value = azurerm_kubernetes_cluster.main.name }
