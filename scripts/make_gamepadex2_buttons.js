import * as fsp from 'node:fs/promises';

const GAMEPAD_EX2_PATH = 'TeamCode/src/main/kotlin/org/firstinspires/ftc/teamcodekt/blacksmith/listeners/ReforgedGamepad.kt';

(async () => {
  let generated_code = '\n';

  getButtons().forEach(buttonSlashPadding => {
    switch(true) {
      case typeof buttonSlashPadding === "string":
        generated_code += `    // -- ${buttonSlashPadding} --\n`; // Spacer to group together related buttons
        break;
      case button.type === 'boolean':
        generated_code += generateBooleanButton(buttonSlashPadding.name) + '\n\n';
        break;
      case button.type === 'float':
        generated_code += generateFloatButton(buttonSlashPadding.name) + '\n\n';
        break;
    }
  });

  generated_code = generated_code.slice(0, -1); // Remove extra newline

  let template = await fsp.readFile(GAMEPAD_EX2_PATH, 'utf-8');

  let generated_file = addCodeIntoTemplate(template, generated_code);

  await fsp.writeFile(GAMEPAD_EX2_PATH, generated_file);
})();

function generateBooleanButton(name) {
  const generated_code = [
    `/**`,
    ` * Allows client to perform an action when the gamepad's '${name}' button's state is mutated.`,
    ` * \`\`\`java`,
    ` * //e.g:`,
    ` * gamepad_x1.${name}.${randomUsageFunction()}(this::doSomething);`,
    ` */`,
    `@JvmField`,
    `val ${name} = GamepadBooleanListener(gamepad::${name})`,
  ]

  return generated_code.map(line => `    ${line}`).join('\n');
}

function generateFloatButton(name) {
  const random_usage_function = randomUsageFunction();

  const generated_code = [
    `/**`,
    ` * Allows client to perform an action when the gamepad's '${name}' button's state is mutated.`,
    ` * \`\`\`java`,
    ` * //e.g: (Triggers when abs(${name}) > .5)`,
    ` * gamepad_x1.${name}.${random_usage_function}(this::doSomething);`,
    ` */`,
    `@JvmField`,
    `val ${name} = ${name}(deadzone = .5)`,
    ``,
    `/**`,
    ` * Allows client to perform an action when the gamepad's '${name}' button's state is mutated.`,
    ` * \`\`\`java`,
    ` * //e.g: (Triggers when abs(${name}) > the_given_deadzone)`,
    ` * gamepad_x1.${name}(.1).${random_usage_function}(this::doSomething);`,
    ` * \`\`\``,
    ` * @param deadzone The minimum value that the ${name} must be above to trigger the event.`,
    ` */`,
    `fun ${name}(deadzone: Double): GamepadAnalogueListener {`,
    `    return GamepadAnalogueListener(deadzone, gamepad::${name})`,
    `}`,
  ];

  return generated_code.map(line => `    ${line}`).join('\n');
}

function addCodeIntoTemplate(template, code) {
  const template_start_index = template.indexOf('// -- START MACHINE GENERATED CODE --') + '// -- START MACHINE GENERATED CODE --'.length;
  const template_end_index = template.indexOf('    // -- END MACHINE GENERATED CODE --');

  return (
    template.substring(0, template_start_index) +
    '\n' +
    code +
    '\n' +
    template.substring(template_end_index)
  );
}

function randomUsageFunction() {
  return [
    'onRise',
    'onFall',
    'whileHigh',
    'whileLow',
  ][~~(Math.random() * 4)];
}

function getButtons() {
  return [
    'Main gamepad buttons',
    { type: 'boolean', name: 'a' },
    { type: 'boolean', name: 'b' },
    { type: 'boolean', name: 'x' },
    { type: 'boolean', name: 'y' },
    'Dpad',
    { type: 'boolean', name: 'dpad_up' },
    { type: 'boolean', name: 'dpad_down' },
    { type: 'boolean', name: 'dpad_left' },
    { type: 'boolean', name: 'dpad_right' },
    'Bumpers',
    { type: 'boolean', name: 'left_bumper' },
    { type: 'boolean', name: 'right_bumper' },
    'Joysticks',
    { type: 'float',   name: 'left_stick_x' },
    { type: 'float',   name: 'left_stick_y' },
    { type: 'float',   name: 'right_stick_x' },
    { type: 'float',   name: 'right_stick_y' },
    'Triggers',
    { type: 'float',   name: 'left_trigger' },
    { type: 'float',   name: 'right_trigger' },
    'Joystick buttons',
    { type: 'boolean', name: 'left_stick_button' },
    { type: 'boolean', name: 'right_stick_button' },
    'PS4 controller buttons',
    { type: 'boolean', name: 'circle' },
    { type: 'boolean', name: 'cross' },
    { type: 'boolean', name: 'triangle' },
    { type: 'boolean', name: 'square' },
    'Random buttons',
    { type: 'boolean', name: 'share' },
    { type: 'boolean', name: 'options' },
    { type: 'boolean', name: 'guide' },
    { type: 'boolean', name: 'start' },
    { type: 'boolean', name: 'back' },
    'Touchpad buttons & triggers',
    { type: 'boolean', name: 'touchpad' },
    { type: 'boolean', name: 'touchpad_finger_1' },
    { type: 'boolean', name: 'touchpad_finger_2' },
    { type: 'float',   name: 'touchpad_finger_1_x' },
    { type: 'float',   name: 'touchpad_finger_1_y' },
    { type: 'float',   name: 'touchpad_finger_2_x' },
    { type: 'float',   name: 'touchpad_finger_2_y' },
    'Whatever this is',
    { type: 'boolean', name: 'ps' },
  ];
}
