<?php
    $mysqli = false;
    function connect_db(){
        global $mysqli;
        $mysqli = new mysqli("WebTech", "root", "", "db");
        if($mysqli->connect_error){
            die("Database connection error");
        } else {

        }
        $mysqli->query("SET NAMES utf8mb4");
    }
    function close_db(){
        global $mysqli;
        $mysqli->close();
    }
    function insert_image($new_img){
        global $mysqli;
        connect_db();
        $query = "INSERT INTO `images` (`src`, `user`, `title`, `date`, `location`) VALUES ('{$new_img['src']}', '{$new_img['user']}', '{$new_img['title']}', '{$new_img['date']}', '{$new_img['location']}');";
        $result = $mysqli->query($query);
        close_db();
        return $result;
    }
    function get_all(){
        global $mysqli;
        connect_db();
        $query = "SELECT * FROM `images`;";
        $result = $mysqli->query($query);
        close_db();
        return json_encode(toArray($result));
    }
    function remove($id){
        global $mysqli;
        connect_db();
        $query = "DELETE FROM `images` WHERE `id`={$id};";
        $result = $mysqli->query($query);
        close_db();
        return $result;
    }
    function update($mod_img){
        global $mysqli;
        connect_db();
        $query = "UPDATE `images` SET `user`='{$mod_img['user']}', `title`='{$mod_img['title']}', `date`='{$mod_img['date']}', `location`='{$mod_img['location']}' WHERE `id`={$mod_img['id']};";
        $result = $mysqli->query($query);
        close_db();
        return $result;
    }
    function toArray($sqlresult){
        $array = array ();
        while(($row = $sqlresult->fetch_assoc()) != false){
            $array[] = $row;
        }          
        return $array;
    }

    $method = $_GET['qt'];
    switch($method){
        case 'add':{
            $img = json_decode($_GET['v'], true);
            $result = insert_image($img);
            echo $result;
            break;
        }
        case 'all':{
            $result = get_all();
            echo $result;
            break;
        }
        case 'delete':{
            $id = (int)$_GET['v'];
            $result = remove($id);
            echo $result;
            break;
        }
        case 'update':{
            $img = json_decode($_GET['v'], true);
            $result = update($img);
            echo $result;
            break;
        }
    }

    
    
?>