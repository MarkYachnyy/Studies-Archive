resource "yandex_vpc_network" "practice" {
  name = "practice"
  description = "Network for practice"
  folder_id = var.folder_id
}

resource "yandex_vpc_subnet" "practice" {
  name = "practice-subnet"
  v4_cidr_blocks = ["10.10.10.0/24"]
  zone = var.zone
  network_id = yandex_vpc_network.practice.id
  folder_id = var.folder_id
  description = "Subnet for practice virtual machine"
}

