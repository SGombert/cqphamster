<template>
  <q-page class="flex flex-center" style="background-image:url('statics/library2.jpg');background-repeat:no-repeat;background-size:cover;color:white!important;">
      <div class="h-cl"><h1 class="text-h1">cqpHamster</h1></div>

    <q-card class="my-card bg-white text-white" elevated>
      <q-card-section>
        <div class="text-h6" style="color:rgba(0, 0, 0, 0.54);">Login</div>
      </q-card-section>

      <q-card-section>
      <q-form class="q-gutter-md" style="margin:15px;">
        <q-input
        class="h-cl"
          v-model="name"
          label="E-Mail"
          hint="E-Mail"
          lazy-rules
          :rules="[ val => val && val.length > 0 || 'Please type something']"
        />

      <q-input v-model="password" :type="isPwd ? 'password' : 'text'" hint="Password" class="h-cl ">
        <template v-slot:append>
          <q-icon
            :name="isPwd ? 'visibility_off' : 'visibility'"
            class="cursor-pointer"
            @click="isPwd = !isPwd"
          />
        </template>
      </q-input>
      </q-form>
      </q-card-section>

      <q-card-actions class="bg-primary">
        <q-btn flat>Login</q-btn>
        <q-btn flat>Register</q-btn>
        <q-btn flat>Forgot Password</q-btn>
      </q-card-actions>
    </q-card>

      <div class="h-cl"><h4 class="text-h4">Corpus and Copmputational Linguistics Group, Inst. Ling. and Lit. Sci., Dep. Hist. and soc. Sci.</h4></div>
  </q-page>
</template>

<style>
  .h-cl {
    text-shadow:0px 0px 1px white;
    text-align:center;
    width:100%;
    margin-left:auto;
    margin-right:auto;
  }

</style>

<script>
export default {
  name: "Index"
};
</script>
