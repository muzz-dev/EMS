package com.example.demo.controller;

import com.example.demo.dao.DeploymentRepository;
import com.example.demo.entities.Deployment;
import com.example.demo.entities.response.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/deploy/")
public class DeploymentController {

    @Value("file.upload.location")
    private String uploadFolder;

    @Autowired
    DeploymentRepository deployRepo;

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @GetMapping("/getAll")
    public List<Deployment> getAllDeployment()
    {
        return deployRepo.findActiveDeployment();
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @RequestMapping(value = "/create" , method = RequestMethod.POST, consumes = { "multipart/form-data"})
    public ResponseEntity<MessageResponse> create (@RequestPart("deployment") String deployment,@RequestPart("logo")MultipartFile logo) throws IOException {
        System.out.println(deployment);
        Deployment d1 = new Deployment();
        ObjectMapper objectMapper = new ObjectMapper();
        d1= objectMapper.readValue(deployment,Deployment.class);
        d1.setLogo(logo.getBytes());
        d1.setActive(true);
        deployRepo.save(d1);

        return ResponseEntity.ok(new MessageResponse("Deployment created with name :" + d1.getDeploymentName()));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @PutMapping("/edit")
    public Deployment edit(@RequestBody Deployment deployment)
    {
        Optional<Deployment> deploy = deployRepo.findById(deployment.getId());
        if(!deploy.isPresent())
            throw new RuntimeException("Deployment with specified id is not found");

        if(deployment.getDeploymentName() != null)
            deploy.get().setDeploymentName(deployment.getDeploymentName());
        if(deployment.getCompanyName() != null)
            deploy.get().setCompanyName(deployment.getCompanyName());
        if(deployment.getLogo() !=null)
            deploy.get().setLogo(deployment.getLogo());

        deployRepo.save(deploy.get());
        return deployment;
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponse> delete(@RequestParam(name = "id")int id)
    {
        Optional<Deployment> deploy = deployRepo.findById(id);
        if(!deploy.isPresent())
            throw new RuntimeException("Deployment with specified id is not found");

        if(deploy.get().getUser().size() != 0)
            throw  new RuntimeException("Can't delete deployment associated user exist");
       else {
            deploy.get().setActive(false);
            deployRepo.save(deploy.get());
            return ResponseEntity.ok(new MessageResponse("Deployment deleted with name :" + deploy.get().getDeploymentName()));
        }
    }

//    @GetMapping("/test")
//    public ResponseEntity<byte[]> test()
//    {
//        BufferedImage img=null;
//        List<Deployment> deploymentList = deployRepo.findActiveDeployment();
//        for (Deployment temp : deploymentList)
//        {
//            try {
//                byte[] imageString = temp.getLogo();
//                img = ImageIO.read(new ByteArrayInputStream(imageString));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            //    imageByte = Base64.getDecoder().decode(imageString);
//        }
//      System.out.println(img.toString());
//    }

    @GetMapping("/test")
    public ResponseEntity<byte[]> test() {

        byte[] imageString = null;
        List<Deployment> deploymentList = deployRepo.findActiveDeployment();
        for (Deployment temp : deploymentList) {
            imageString = temp.getLogo();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageString);

    }
}
