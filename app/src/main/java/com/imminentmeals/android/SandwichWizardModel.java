package com.imminentmeals.android;

import android.content.Context;
import com.imminentmeals.android.wizard.BranchPage;
import com.imminentmeals.android.wizard.CustomerInfoPage;
import com.imminentmeals.android.wizard.MultipleFixedChoicePage;
import com.imminentmeals.android.wizard.PageList;
import com.imminentmeals.android.wizard.SingleFixedChoicePage;
import com.imminentmeals.android.wizard.WizardModel;

public class SandwichWizardModel extends WizardModel {

  public SandwichWizardModel(Context context) {
    super(context);
  }

  @Override protected PageList onNewRootPageList() {
    return new PageList(new BranchPage(this, "Order type").addBranch("Sandwich",
                                                                     new SingleFixedChoicePage(this,
                                                                                               "Bread")
                                                                         .addChoices("White",
                                                                                     "Wheat", "Rye",
                                                                                     "Pretzel",
                                                                                     "Ciabatta")
                                                                         .require(),

                                                                     new MultipleFixedChoicePage(this,
                                                                                                 "Meats")
                                                                         .addChoices("Pepperoni",
                                                                                     "Turkey",
                                                                                     "Ham",
                                                                                     "Pastrami",
                                                                                     "Roast Beef",
                                                                                     "Bologna"),

                                                                     new MultipleFixedChoicePage(this,
                                                                                                 "Veggies")
                                                                         .addChoices("Tomatoes",
                                                                                     "Lettuce",
                                                                                     "Onions",
                                                                                     "Pickles",
                                                                                     "Cucumbers",
                                                                                     "Peppers"),

                                                                     new MultipleFixedChoicePage(this,
                                                                                                 "Cheeses")
                                                                         .addChoices("Swiss",
                                                                                     "American",
                                                                                     "Pepperjack",
                                                                                     "Muenster",
                                                                                     "Provolone",
                                                                                     "White American",
                                                                                     "Cheddar",
                                                                                     "Bleu"),

                                                                     new BranchPage(this,
                                                                                    "Toasted?").addBranch("Yes",
                                                                                                          new SingleFixedChoicePage(this,
                                                                                                                                    "Toast time")
                                                                                                              .addChoices("30 seconds",
                                                                                                                          "1 minute",
                                                                                                                          "2 minutes"))
                                                                                               .addBranch("No")
                                                                                               .value("No"))

                                                          .addBranch("Salad",
                                                                     new SingleFixedChoicePage(this,
                                                                                               "Salad type")
                                                                         .addChoices("Greek",
                                                                                     "Caesar")
                                                                         .require(),

                                                                     new SingleFixedChoicePage(this,
                                                                                               "Dressing")
                                                                         .addChoices("No dressing",
                                                                                     "Balsamic",
                                                                                     "Oil & vinegar",
                                                                                     "Thousand Island",
                                                                                     "Italian")
                                                                         .value("No dressing"))

                                                          .require(),

                        new CustomerInfoPage(this, "Your info").require());
  }
}
