-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: sandyafashioncorner
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `color`
--

DROP TABLE IF EXISTS `color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color` (
  `id` int NOT NULL,
  `color` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color`
--

LOCK TABLES `color` WRITE;
/*!40000 ALTER TABLE `color` DISABLE KEYS */;
INSERT INTO `color` VALUES (1,'#FF0000'),(2,'#0000FF'),(3,'#000000'),(4,'#FFFFFF'),(5,'#00FF00'),(6,'#FFFF00'),(7,'#808080'),(8,'#8B4513');
/*!40000 ALTER TABLE `color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color_has_item_has_size`
--

DROP TABLE IF EXISTS `color_has_item_has_size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color_has_item_has_size` (
  `id` int NOT NULL,
  `color_id` int NOT NULL,
  `item_has_size_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_color_has_item_has_size_item_has_size1_idx` (`item_has_size_id`),
  KEY `fk_color_has_item_has_size_color1_idx` (`color_id`),
  CONSTRAINT `fk_color_has_item_has_size_color1` FOREIGN KEY (`color_id`) REFERENCES `color` (`id`),
  CONSTRAINT `fk_color_has_item_has_size_item_has_size1` FOREIGN KEY (`item_has_size_id`) REFERENCES `item_has_size` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color_has_item_has_size`
--

LOCK TABLES `color_has_item_has_size` WRITE;
/*!40000 ALTER TABLE `color_has_item_has_size` DISABLE KEYS */;
INSERT INTO `color_has_item_has_size` VALUES (1,1,1),(2,2,1),(3,1,2),(4,2,3),(5,3,4),(6,3,5),(7,4,6),(8,3,7),(9,7,8),(10,3,9),(11,4,10),(12,2,11),(13,5,12),(14,6,13),(15,2,14),(16,5,15),(17,1,16),(18,3,17),(19,8,18),(20,2,19),(21,8,20);
/*!40000 ALTER TABLE `color_has_item_has_size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int NOT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `phone` varchar(12) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'John','Doe','555-123-4567','john.doe@example.com'),(2,'Jane','Smith','555-234-5678','jane.smith@example.com'),(3,'Michael','Brown','555-345-6789','michael.brown@example.com'),(4,'Emily','Johnson','555-456-7890','emily.johnson@example.com'),(5,'David','Wilson','555-567-8901','david.wilson@example.com'),(6,'Olivia','Taylor','555-678-9012','olivia.taylor@example.com'),(7,'Daniel','Anderson','555-789-0123','daniel.anderson@example.com'),(8,'Sophia','Thomas','555-890-1234','sophia.thomas@example.com'),(9,'James','Martinez','555-901-2345','james.martinez@example.com'),(10,'Isabella','Garcia','555-012-3456','isabella.garcia@example.com');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_has_item_has_size`
--

DROP TABLE IF EXISTS `customer_has_item_has_size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_has_item_has_size` (
  `id` int DEFAULT NULL,
  `customer_id` int NOT NULL,
  `item_has_size_id` int NOT NULL,
  `amount` int NOT NULL,
  `price` double NOT NULL,
  `date` date NOT NULL,
  `item_status_id` int NOT NULL,
  KEY `fk_customer_has_item_has_size_item_has_size1_idx` (`item_has_size_id`),
  KEY `fk_customer_has_item_has_size_customer1_idx` (`customer_id`),
  KEY `fk_customer_has_item_has_size_item_status1_idx` (`item_status_id`),
  CONSTRAINT `fk_customer_has_item_has_size_customer1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_customer_has_item_has_size_item_has_size1` FOREIGN KEY (`item_has_size_id`) REFERENCES `item_has_size` (`id`),
  CONSTRAINT `fk_customer_has_item_has_size_item_status1` FOREIGN KEY (`item_status_id`) REFERENCES `item_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_has_item_has_size`
--

LOCK TABLES `customer_has_item_has_size` WRITE;
/*!40000 ALTER TABLE `customer_has_item_has_size` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_has_item_has_size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `selling_price` double DEFAULT NULL,
  `stock_id` int NOT NULL,
  PRIMARY KEY (`id`,`stock_id`),
  KEY `fk_item_stock1_idx` (`stock_id`),
  CONSTRAINT `fk_item_stock1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'Blue T-Shirt',10,15,1),(2,'Black Jeans',25,35,1),(3,'Leather Jacket',50,75,1),(4,'White Sneakers',30,45,1),(5,'Baseball Cap',5,10,1),(6,'Denim Shorts',20,28,1),(7,'Graphic Hoodie',35,50,1),(8,'Sports Watch',40,60,1),(9,'Sunglasses',15,22,1),(10,'Leather Belt',12,18,1);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_has_size`
--

DROP TABLE IF EXISTS `item_has_size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_has_size` (
  `id` int NOT NULL,
  `item_id` int NOT NULL,
  `item_stock_id` int NOT NULL,
  `size_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_item_has_size_size1_idx` (`size_id`),
  KEY `fk_item_has_size_item1_idx` (`item_id`,`item_stock_id`),
  CONSTRAINT `fk_item_has_size_item1` FOREIGN KEY (`item_id`, `item_stock_id`) REFERENCES `item` (`id`, `stock_id`),
  CONSTRAINT `fk_item_has_size_size1` FOREIGN KEY (`size_id`) REFERENCES `size` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_has_size`
--

LOCK TABLES `item_has_size` WRITE;
/*!40000 ALTER TABLE `item_has_size` DISABLE KEYS */;
INSERT INTO `item_has_size` VALUES (1,1,1,1),(2,1,1,2),(3,1,1,3),(4,2,1,2),(5,2,1,3),(6,2,1,4),(7,3,1,3),(8,3,1,4),(9,3,1,5),(10,4,1,2),(11,4,1,3),(12,5,1,1),(13,5,1,2),(14,6,1,2),(15,6,1,3),(16,7,1,3),(17,7,1,4),(18,8,1,2),(19,9,1,2),(20,10,1,3);
/*!40000 ALTER TABLE `item_has_size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_status`
--

DROP TABLE IF EXISTS `item_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_status` (
  `id` int NOT NULL,
  `status` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_status`
--

LOCK TABLES `item_status` WRITE;
/*!40000 ALTER TABLE `item_status` DISABLE KEYS */;
INSERT INTO `item_status` VALUES (1,'sold'),(2,'returned');
/*!40000 ALTER TABLE `item_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL,
  `role` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin'),(2,'Cashier');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `size`
--

DROP TABLE IF EXISTS `size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `size` (
  `id` int NOT NULL,
  `size` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `size`
--

LOCK TABLES `size` WRITE;
/*!40000 ALTER TABLE `size` DISABLE KEYS */;
INSERT INTO `size` VALUES (1,'SM'),(2,'MD'),(3,'LG'),(4,'XL'),(5,'XXL');
/*!40000 ALTER TABLE `size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock` (
  `id` int NOT NULL,
  `date` datetime DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
INSERT INTO `stock` VALUES (1,'2052-12-02 05:00:00','First Stock');
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(20) DEFAULT NULL,
  `lastName` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`,`role_id`),
  KEY `fk_user_role_idx` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Sandun','Jayalath','sandun@gmail.com',1),(2,'Rukshan','Dissanayake','rukshan@gmail.com',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-21 14:29:46
