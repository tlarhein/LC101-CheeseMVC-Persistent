package org.launchcode.cheesemvc.controllers;

import org.launchcode.cheesemvc.models.Category;
import org.launchcode.cheesemvc.models.Cheese;
import org.launchcode.cheesemvc.models.Menu;
import org.launchcode.cheesemvc.models.data.CheeseDao;
import org.launchcode.cheesemvc.models.data.MenuDao;
import org.launchcode.cheesemvc.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by LaunchCode, assignment revised by Tracey Cannon 0419
 */
@Controller
@RequestMapping(value = "menu")
public class MenuController {
    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model){
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu
            newMenu, Errors errors) {

        if (errors.hasErrors()){
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model){
        model.addAttribute("menu", menuDao.findOne(id));
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(@PathVariable int id, Model model){

        AddMenuItemForm newForm = new AddMenuItemForm(menuDao.findOne(id), cheeseDao.findAll());
        model.addAttribute("form", newForm);
        model.addAttribute("title", "Add item to menu: " + menuDao.findOne(id).getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm form, Errors errors, Model model){

        if (errors.hasErrors()){
            model.addAttribute("form", form);
            model.addAttribute("title", "Add item to menu: " + form.getMenuId());
            return "menu/add-item";
        }

        Cheese aCheese = cheeseDao.findOne(form.getCheeseId());
        Menu aMenu = menuDao.findOne(form.getMenuId());
        aMenu.addItem(aCheese);
        menuDao.save(aMenu);

        return "redirect:view/" + aMenu.getId();
    }
}
