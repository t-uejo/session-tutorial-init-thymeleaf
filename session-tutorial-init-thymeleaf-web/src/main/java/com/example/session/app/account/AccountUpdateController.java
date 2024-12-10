package com.example.session.app.account;

import jakarta.inject.Inject;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.session.app.account.AccountUpdateForm.Wizard1;
import com.example.session.app.account.AccountUpdateForm.Wizard2;
import com.example.session.domain.model.Account;
import com.example.session.domain.service.account.AccountService;
import com.example.session.domain.service.userdetails.AccountDetails;

@Controller
@RequestMapping("account/update")
@SessionAttributes(value = {"accountUpdateForm"})
public class AccountUpdateController {
	@Inject
    AccountService accountService;

    @Inject
    AccountMapper beanMapper;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class,
                new StringTrimmerEditor(true));
    }
    
    @ModelAttribute(value = "accountUpdateForm")
    public AccountUpdateForm setUpAccountForm() {
        return new AccountUpdateForm();
    }
    
    @GetMapping(params = "form1")
    public String showUpdateForm1(
            @AuthenticationPrincipal AccountDetails userDetails,
            AccountUpdateForm form) {

        Account account = accountService.findOne(userDetails.getAccount().getEmail());
        beanMapper.map(account, form);

        return "account/updateForm1";
    }

    @PostMapping(params = "form2")
    public String showUpdateForm2(
            @Validated(Wizard1.class) AccountUpdateForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            return "account/updateForm1";
        }

        return "account/updateForm2";
    }

    @PostMapping(params = "redoForm1")
    public String redoUpdateForm1() {
        return "account/updateForm1";
    }

    @PostMapping(params = "confirm")
    public String confirmUpdate(
            @Validated(Wizard2.class) AccountUpdateForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            return "account/updateForm2";
        }

        return "account/updateConfirm";
    }

    @PostMapping(params = "redoForm2")
    public String redoUpdateForm2() {
        return "account/updateForm2";
    }

    @PostMapping
    public String update(@AuthenticationPrincipal AccountDetails userDetails,
            @Validated({ Wizard1.class, Wizard2.class }) AccountUpdateForm form,
            BindingResult result, 
            RedirectAttributes attributes,
            SessionStatus sessionStatus) {

        if (result.hasErrors()) {
            ResultMessages messages = ResultMessages.error();
            messages.add("e.st.ac.5001");
            throw new IllegalOperationException(messages);
        }

        Account account = beanMapper.map(form);
        accountService.update(account);
        userDetails.setAccount(account);
        attributes.addFlashAttribute("account", account);
        sessionStatus.setComplete();

        return "redirect:/account/update?finish";
    }

    @GetMapping(params = "finish")
    public String finishUpdate() {
        return "account/updateFinish";
    }

    @GetMapping(params = "home")
    public String home(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/goods";
    }
}
