resource "yandex_compute_disk" "practice" {
    name = "practice-boot-disk"
    type = "network-ssd"
    image_id = "fd89miehufm246nh7pi2"
    size = 20
    zone = var.zone
    folder_id = var.folder_id
}

resource "yandex_compute_instance" "practice" {
  name = "practice-vm"
  description = "VM for VSU practice"
  folder_id = var.folder_id
  zone = var.zone

  hostname = "practice.platform.centerctf.ru"

  resources {
    cores = 2
    memory = 4
    core_fraction = 20
  }

  boot_disk {
    auto_delete = true
    disk_id = yandex_compute_disk.practice.id
  }

  network_interface {
    subnet_id = yandex_vpc_subnet.practice.id
    nat = true
    ip_address = "10.10.10.10"
  }

  metadata = {
    user-data = "${file("cloud-config/cloud-init.yml")}"
    serial-port-enable = 1
  }

  scheduling_policy {
    preemptible = true
  }
}

output "public-ip" {
  value = yandex_compute_instance.practice.network_interface[0].nat_ip_address
}
